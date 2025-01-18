/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  InputTransitionBarrier.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2024 Pi4J
 *      * %%
 *     *
 *     * Licensed under the Apache License, Version 2.0 (the "License");
 *     * you may not use this file except in compliance with the License.
 *     * You may obtain a copy of the License at
 *     *
 *     *      http://www.apache.org/licenses/LICENSE-2.0
 *     *
 *     * Unless required by applicable law or agreed to in writing, software
 *     * distributed under the License is distributed on an "AS IS" BASIS,
 *     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     * See the License for the specific language governing permissions and
 *     * limitations under the License.
 *     * #L%
 *     *
 *
 *
 *
 */

package com.pi4j.devices.base_util.gpio;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

/**
 * Tracks state-changes of a DigitalInput, to help facilitate synchronization
 * and to assert expected toggles such as to catch missed or spurious toggles.
 * 
 * For example, if a set of toggles of a given pin are expected, they can be specified
 * to this barrier, then further down in code, problem flow can block, awaiting said toggles to occur.
 * 
 * This barrier can also be used to detect bugs or otherwise uncaught transitions by invoking handlers if
 * unexpected state changes occur (see setToggleConflictHandler and setExhaustedAnticipatedToggleHandler) 
 * or if detected states are different from anticipated states at given points. (see anticipateToggles() )
 * 
 * Note, this class assumes DigitalState.UNKNOWN to be LOW. It is advised that
 * input states are known before creating an InputTransitionBarrier.
 * 
 * @author Chuck Ritola
 *
 */

public final class InputTransitionBarrier implements DigitalStateChangeListener {
    private final DigitalInput inputToMonitor;
    private boolean released = false;
    private boolean lastReceivedState = false;
    private int numAnticipatedToggles = 0;
    private Consumer<Throwable> throwableHandler = null;
    private Integer maxAnticipatedToggles = null;
    private boolean trackingToggleCounts = true;
    private ExhaustedAnticipatedToggleConflictHandler exhaustedAnticipatedToggleHandler = null;
    private AwaitToggleConflictHandler toggleConflictHandler = null;
    private AnticipateTogglesConflictHandler anticipateTogglesConflictHandler = null;
    
    public InputTransitionBarrier(DigitalInput inputToMonitor) {
	this.inputToMonitor = inputToMonitor;
	inputToMonitor.addListener(this);
	lastReceivedState = inputToMonitor.state() == DigitalState.HIGH;
    }//end constructor
    
    /**
     * Cleanup method to remove this barrier from the DigitalInput's listeners.
     * 
     * @since Dec 15, 2024
     */
    public void release() {
	if(released)
	    return;
	inputToMonitor.removeListener(this);
	released = true;
    }//end release()
    
    /**
     * When toggles are awaited where the requested final state does not match the last anticipated final state,
     * a toggle conflict is evident. The AwaitToggleConflictHandler attempts to resolve or otherwise respond to
     * these conflicts.
     * @author Chuck Ritola
     *
     */
    public interface AwaitToggleConflictHandler {
	public boolean handleAwaitToggleConflict(InputTransitionBarrier barrier, boolean lastTrackedAnticipatedState, boolean lastExpectedAnticipatedState);
    }
    
    /**
     * When a state toggle occurs when none are anticipated, an exhausted toggle conflict is evident.
     * The UnanticipatedToggleConflictHandler attempts to resolve or otherwise respond to these conflicts.
     * @author Chuck Ritola
     *
     */
    public interface ExhaustedAnticipatedToggleConflictHandler {
	public boolean handleExhaustedToggleConflict(InputTransitionBarrier barrier, DigitalStateChangeEvent<?> offendingEvent);
    }
    
    public interface AnticipateTogglesConflictHandler {
	public boolean handleAnticipatedToggleConflict(InputTransitionBarrier barrier, int numAnticipated, boolean expectedInitialState);
    }

    @Override
    public synchronized void onDigitalStateChange(@SuppressWarnings("rawtypes") DigitalStateChangeEvent event) throws IllegalStateException {
	if(event.source() == inputToMonitor)
	 handleDigitalStateChangeEvent(event);
    }//end onDigitalStateChange(...)
    
    private synchronized void handleDigitalStateChangeEvent(DigitalStateChangeEvent<?> event) {
	try {
	    final int numAnticipatedToggles = getNumAnticipatedToggles();
	    final boolean newState = event.state().isHigh();
	    final boolean anticipated = getNextAnticipatedState();
	    this.lastReceivedState = newState;
	    
	    if(trackingToggleCounts) {
		if(newState != anticipated) {
			final boolean skip = handleAwaitToggleConflict(newState);
			if(skip) return;
		} else if(numAnticipatedToggles <= 0) {
		    if(exhaustedAnticipatedToggleHandler != null) {
			    final boolean skip = exhaustedAnticipatedToggleHandler.handleExhaustedToggleConflict(this, event);
			    if(skip) return;
		    } else throw new IllegalStateException("Anticipted toggle exhaustion: "+event+" where none anticipated. Last expected state:"+(anticipated?"HIGH":"LOW"));
		}//end if(toggles<=0)
		
		if(this.numAnticipatedToggles > 0)
		    this.numAnticipatedToggles--;
	    }//end if(trackingToggleCounts)
	    
	    notifyAll();
	} catch(Throwable t) {
	    final Consumer<Throwable> th = getThrowableHandler();
	    if(th != null)
		th.accept(t);
	    else
		t.printStackTrace();
	}//end catch()
    }//end handleDigitalStatechangeEvent(...)
    
    /**
     * Block until all anticipated toggles complete or timeout
     * @throws InterruptedException
     * @since Dec 15, 2024
     */
    public synchronized void awaitAnticipatedToggles(long timeoutMillis) throws InterruptedException, TimeoutException {
	final long endTime = System.currentTimeMillis()+timeoutMillis;
	while(this.numAnticipatedToggles > 0 & System.currentTimeMillis() < endTime)
	    wait(timeoutMillis);
    }//end awaitAnticipatedToggles()
    
    private boolean handleAwaitToggleConflict(boolean newState) {
	if(toggleConflictHandler != null)
	    return toggleConflictHandler.handleAwaitToggleConflict(this, this.lastReceivedState, getLastAnticipatedState());
	else
	    throw new IllegalStateException("Expected state: "+(this.getLastAnticipatedState()?"HIGH":"LOW")+", got "+(newState?"HIGH":"LOW"));
    }//end handleToggleConflict(...)
    
/**
 * Block until all anticipated toggles complete and handle a toggle conflict if the expected final state does not match the anticipated final state based
 * the the current anticipated toggle count.
 * @param expectedFinalState
 * @throws InterruptedException
 * @since Dec 15, 2024
 */
    public synchronized void awaitAnticipatedToggles(boolean expectedFinalState, long timeoutMillis) throws InterruptedException, TimeoutException {
	final boolean lastAnticipatedState = getLastAnticipatedState();
	if(expectedFinalState != lastAnticipatedState)
	    handleAwaitToggleConflict(expectedFinalState);
	awaitAnticipatedToggles(timeoutMillis);
    }
    
    /**
     * Instruct the Barrier to anticipate the specified number of toggles, in addition to whatever existing number of toggles are expected.
     * @param expectedInitialState The expected state before the anticipated toggle, with a handled toggle conflict in the case of a mismatch.
     * @param numToggles Number of additional toggles to anticipate.
     * @throws IllegalArgumentException If, after this operation, the total number of anticipated toggles exceeds that set by the MaxAnticipatedToggles property.
     * @since Dec 15, 2024
     */
    public synchronized void anticipateToggles(boolean expectedInitialState, int numToggles) {
	final boolean lastAnticipatedState = getLastAnticipatedState();
	if(expectedInitialState != lastAnticipatedState) {
	    final AnticipateTogglesConflictHandler handler = getAnticipateTogglesConflictHandler();
	    boolean skip;
	    if(handler != null)
		skip = handler.handleAnticipatedToggleConflict(this, numToggles, expectedInitialState);
	    else
		throw new IllegalStateException("Expected initial state is "+expectedInitialState+" but calculated lastAnticipatedState is "+lastAnticipatedState);
	    if(skip) return;
	    }
	this.numAnticipatedToggles += numToggles;
	final Integer maxAT = getMaxAnticipatedToggles();
	final int numAnticipatedToggles = this.numAnticipatedToggles;
	if(maxAT != null && numAnticipatedToggles > maxAT)
	    throw new IllegalStateException("Exceeded maximum exceeded toggles of "+maxAT+". Got "+numAnticipatedToggles+".");
    }//end anticipateToggles(...)
    
    /**
     * Reset the last received state and the number of anticipated toggles is reset to zero.
     * @param initialState The state which this Barrier should consider its last received state.
     * @since Dec 15, 2024
     */
    public synchronized void reset(boolean initialState) {
	this.lastReceivedState = initialState;
	this.numAnticipatedToggles = 0;
    }
    
    public synchronized void reset() {
	reset(inputToMonitor.isOn());
    }
    
    /**
     * Manually submit a state change event as if it came from the DigitalInput supplied to the constructor.
     * This should only be used in corner cases where the internal state of this Barrier must be "nudged."
     * @param event
     * @since Dec 15, 2024
     */
    public void submitManualEvent(DigitalStateChangeEvent<?> event) {
	handleDigitalStateChangeEvent(event);
    }

    protected boolean isReleased() {
        return released;
    }

    protected void setReleased(boolean released) {
        this.released = released;
    }
    
    /**
     * Query the last end-state received from the assigned DigitalInput.
     * @return true if last state was HIGH, false if last state was LOW.
     * @since Dec 16, 2024
     */
    public boolean getLastReceivedState() {
	return lastReceivedState;
    }
    
    public void setLastReceivedState(boolean state) {
	this.lastReceivedState = state;
    }

    /**
     * Queries the next anticipated state based on the toggling of last received state.
     * @return Next anticipated state
     * @since Dec 15, 2024
     */
    public boolean getNextAnticipatedState() {
        return !getLastReceivedState();
    }

    /**
     * 
     * @return The input assigned to this Barrier's constructor whose state is to be monitored
     * @since Dec 15, 2024
     */
    public DigitalInput getInputToMonitor() {
        return inputToMonitor;
    }

    /**
     * 
     * @return	The current number of anticipated toggles remaining.
     * @since Dec 15, 2024
     */
    public int getNumAnticipatedToggles() {
        return numAnticipatedToggles;
    }
    
    
    /**
     * Calculate the expected state based on the last state change detected and the number of anticipated toggles.
     * @return The expected state after all anticipated toggles have occurred.
     * @since Dec 15, 2024
     */
    public boolean getLastAnticipatedState() {
	//final boolean flip = (Math.abs(this.numAnticipatedToggles) % 2)==1;
	final boolean flip = (this.numAnticipatedToggles & 0x1) != 0;
	return flip?!this.lastReceivedState:this.lastReceivedState;
    }

    public Consumer<Throwable> getThrowableHandler() {
        return throwableHandler;
    }

    /**
     * Specify a handler in case a Throwable is thrown while processing a DigitalInput's state change event, which usually occurs from
     * another thread. As such, without this handler, throwables may be caught and discarded by the outside thread. If anticipated toggles are detected and no
     * exhaustedAnticipatedToggleHandler property is set, it will fall back to a thrown IllegalStateException and be fed to the throwable Handler specified in this method.
     * @param throwableHandler A consumer to handle throwables thrown while processing a state change event.
     * @since Dec 15, 2024
     */
    public void setThrowableHandler(Consumer<Throwable> throwableHandler) {
        this.throwableHandler = throwableHandler;
    }

    public Integer getMaxAnticipatedToggles() {
        return maxAnticipatedToggles;
    }

    public void setMaxAnticipatedToggles(Integer maxAnticipatedToggles) {
        this.maxAnticipatedToggles = maxAnticipatedToggles;
    }

    /**
     * Specify whether or not toggle-counting from the assigned DigitalInput should be enabled.
     * Used for corner-cases.
     * @param track	True to track digital state change events and decrement the anticipated toggles, else false to ignore those events.
     * @since Dec 16, 2024
     */
    public synchronized void setTrackToggleCounts(boolean track) {
	this.trackingToggleCounts = track;
    }

    public ExhaustedAnticipatedToggleConflictHandler getExhaustedAnticipatedToggleHandler() {
        return exhaustedAnticipatedToggleHandler;
    }

    /**
     * This handler is invoked when a toggle is detected but no remaining toggles were anticipated.
     * @param newHandler
     * @since Dec 15, 2024
     */
    public void setExhaustedAnticipatedToggleHandler(
    	ExhaustedAnticipatedToggleConflictHandler newHandler) {
        this.exhaustedAnticipatedToggleHandler = newHandler;
    }

    public AwaitToggleConflictHandler getAwaitToggleConflictHandler() {
        return toggleConflictHandler;
    }

    /**
     * Set the handler invoked when the anticipated final state after anticipated toggles complete conflicts with the
     * expected final state based on received DigitalStateChangeEvents from the assigned DigitalInput after after anticipated toggles complete.
     * @param toggleConflictHandler A Consumer which accepts a Boolean representing the most recent DigitalStateChangeEvent state() value where true is HIGH and false is LOW or UNKNOWN
     * @since Dec 16, 2024
     */
    public void setAwaitToggleConflictHandler(
    	AwaitToggleConflictHandler toggleConflictHandler) {
        this.toggleConflictHandler = toggleConflictHandler;
    }

    public void setNumAnticipatedToggles(int numAnticipatedToggles) {
        this.numAnticipatedToggles = numAnticipatedToggles;
    }

    /**
     * 
     * @return true if this Barrier is currently tracking toggle counts, else false.
     * @since Dec 15, 2024
     */
    public boolean isTrackingToggleCounts() {
        return trackingToggleCounts;
    }

    /**
     * Used to temporarily ignore toggle count events from the assigned DigitalInput. Intended for corner cases.
     * @param trackingToggleCounts true to continue tracking toggle count events from the assigned DigitalInput, false to ignore them.
     * @since Dec 15, 2024
     */
    public void setTrackingToggleCounts(boolean trackingToggleCounts) {
        this.trackingToggleCounts = trackingToggleCounts;
    }

    public AnticipateTogglesConflictHandler getAnticipateTogglesConflictHandler() {
        return anticipateTogglesConflictHandler;
    }

    public void setAnticipateTogglesConflictHandler(
    	AnticipateTogglesConflictHandler anticipateTogglesConflictHandler) {
        this.anticipateTogglesConflictHandler = anticipateTogglesConflictHandler;
    }
}//end InputTransitionBarrier
