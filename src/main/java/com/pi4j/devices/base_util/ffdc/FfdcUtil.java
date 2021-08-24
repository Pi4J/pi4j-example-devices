/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  FfdcUtil.java
 *  *   *
 *  *   * This file is part of the Pi4J project. More information about
 *  *   * this project can be found here:  https://pi4j.com/
 *  *   * **********************************************************************
 *    * %%
 *  *   * Copyright (C) 2012 - 2021 Pi4J
 *     * %%
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *    * #L%
 *  *
 *  *
 *
 *
 */

package com.pi4j.devices.base_util.ffdc;

import com.pi4j.devices.base_util.PrintInfo;
import com.pi4j.context.Context;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import com.pi4j.util.Console;
import org.apache.logging.log4j.Logger;


/**
 * FfdcUtil, implements FfdcLoggingModule and FfdcLoggingSystem. Provides basic logging
 * functionality with prepended default text to better describe each entry.
 * <ul>
 * <li>Trace – Lowest level. Used to track the application flow.
 * <li>Debug – used to add diagnostic messages of the application. Generally useful for debugging when there is an error.
 * <li>Info – Used to indicate important flows of the application.
 * <li>Warn – Used to indicate potential error scenarios of the application.
 * <li>Error – Used to log errors and exceptions of the application.
 * </ul>
 *
 * <p>
 *     Assumes the logging configuration file 'log4j2.properties' is located in the class path.
 * </p>
 */


public class FfdcUtil implements FfdcLoggingModule, FfdcLoggingSystem {

    /**
     * FfdcUtil CTOR
     * <ul>
     *     <li> Instantiated Console class
     *     <li>Instantiated Context class
     *     <li> Vaule used in logging determination
     *     <li> Class owning the loged data. Significant as this is
     *     log4j2.properties file, and must match
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable, logging object instantiated.
     */
    public FfdcUtil(Console console, Context pi4j, int ffdcControlLevel, Class owner) {
        // this.context = context;
        this.console = console;
        this.pi4j = pi4j;
        this.ffdc = ffdcControlLevel;
        this.owner =  owner;
        this.init();
    }

    /**
     * init
     * Uses log4j v2  assumes location of the log4j2.properties configuration file
     * is on classpath
     */
    private void init() {
        this.logger = LogManager.getLogger(owner);
        this.setLevel(this.ffdc);
    }

    Console console = null;
    Context pi4j;
    int ffdc;
    Logger logger = null;
    Class owner ;


    /**
     * ffdcMethodEntry
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  &gt;&gt;&gt;&gt;  Entered : prepended to detail
     *           </p>
     *
     */
    @Override
    public boolean ffdcMethodEntry(String detail) {
        this.logger.info(">>>>  Entered :" + detail);
        return (true);
    }

    /**
     * ffdcMethodExit
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  &lt;&lt;&lt;&lt;  Exit : prepended to detail
     *           </p>
     *
     */
    @Override
    public boolean ffdcMethodExit(String detail) {
        this.logger.info("<<<<<  Exit :" + detail);
        return (true);
    }

    /**
     * ffdcConfigWarningEntry
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  ......  Warning :' prepended to detail
     *           </p>
     *
     */
    @Override
    public boolean ffdcConfigWarningEntry(String detail) {
        this.logger.warn("......  Warning :" + detail);
        return (true);
    }

    /**
     * ffdcDebugEntry
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  'Info :' prepended to detail
     *           </p>
     *
     */
    @Override
    public boolean ffdcDebugEntry(String detail) {
        this.logger.debug("Info :" + detail);
        return (true);
    }

    /**
     * ffdcErrorEntry
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  '??????  ERROR :' prepended to detail
     *           </p>
     *
     */
    @Override
    public boolean ffdcErrorEntry(String detail) {
        this.logger.error("??????  ERROR :" + detail);
        return (true);
    }

    /**
     * ffdcErrorExit
     * <p>
     * @param detail to log
     *   @param code  Error code for exit
     *              </p>
     *               <p>
     * PostCondition  will first create a log entry, then error exit the application
     *       passing the value of 'code'
     *           </p>
     *
     */
    /**
      ffdcErrorExit,
     */
    @Override
    public void ffdcErrorExit(String detail, int code) {
        this.logger.fatal("Fatal exit :" + detail + "\n  ");
        this.ffdcFlushShutdown();
        this.console.print("error exit " + code);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.dumpStack();
        this.pi4j.shutdown();
        System.exit(code);
    }


    /**
     * ffdcClearLogs
     * <p>
     * @param detail to log
     * </p>
     *               <p>
     * PostCondition  logs cleared
     *           </p>
     *
     */
     public boolean ffdcClearLogs(String detail) {
        // TODO
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration conf = ctx.getConfiguration();
        LoggerConfig loggerConfig = conf.getLoggerConfig(this.owner.getName());
        this.logger.debug("Info : Logs cleared " + detail);

        return (true);
    }

    /**
     * ffdcFlushShutdown
     *
     * PostCondition  Log manager shut down, records flushed to file.
     *
     *
     */
    @Override
    public boolean ffdcFlushShutdown() {
        LogManager.shutdown();
        return(true);
    }


    @Override
    public void printLoadedPlatforms() {
        PrintInfo.printLoadedPlatforms(this.console, this.pi4j);
    }

    @Override
    public void printDefaultPlatform() {
        PrintInfo.printDefaultPlatform(this.console, this.pi4j);
    }

    @Override
    public void printProviders() {
        PrintInfo.printProviders(this.console, this.pi4j);
    }

    @Override
    public void printRegistry() {
        PrintInfo.printRegistry(this.console, this.pi4j);
    }


    /**
     * setLevel. Allow user to modify the log level in use by the application
     *
     * @param val new logging level
     *            Allowable values
     *            <ul>
     *            <li> 0 ALL/TRACE
     *            <li> 1 DEBUG
     *            <li> 2 INFO
     *            <li> 3 WARN
     *            <li> 4 ERROR
     *            <li> 5 FATAL
     *            <li> 6 OFF
     *            </ul>
     * @return if level 0-6 return true, else false
     */
    //0 ALL < TRACE < 1 DEBUG < 2 INFO < 3 WARN < 4 ERROR < 5 FATAL < OFF  public final static int OFF_INT = Integer.MAX_VALUE;
    public boolean setLevel(int val) {
        boolean rval = true;
        if ((val >= 0) && (val < 7)) {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration conf = ctx.getConfiguration();

            //LoggerContext ctx1 = (LoggerContext)LogManager.getContext(false);
            //AbstractConfiguration configuration = (AbstractConfiguration) ctx.getConfiguration();

            if (conf.getLoggerConfig(this.owner.getName()) != null) {
                LoggerConfig loggerConfig = conf.getLoggerConfig(this.owner.getName());
                loggerConfig.setLevel(this.mapIntToLevel(val));
            } else {
                LoggerConfig loggerConfig = new LoggerConfig(this.owner.getName(), this.mapIntToLevel(val), true);
                conf.addLogger(this.owner.getName(), loggerConfig);
            }

            ctx.updateLoggers(conf);
        } else {
            rval = false;
        }
        return (rval);
    }

    /**
     *   mapIntToLevel
     * @param newLevel
     * @return Simple int converted to Log4j value
     */
    private Level mapIntToLevel(int newLevel) {
        Level rval = Level.ERROR;
        switch (newLevel) {
            case 0: {
                rval = Level.ALL;
                break;
            }case 1: {
                rval = Level.DEBUG;
                break;
            }
            case 2: {
                rval = Level.INFO;
                break;
            }
            case 3: {
                rval = Level.WARN;
                break;
            }
            case 4: {
                rval = Level.ERROR;
                break;
            }
            case 5: {
                rval = Level.FATAL;
                break;
            }
            case 6: {
                rval = Level.OFF;
                break;
            }
            default: {
                this.ffdcConfigWarningEntry("invalid parm : " + newLevel);
            }
        }
        return (rval);
    }




    private void initLogger() {
        //  nothing required at this time
    }

}
