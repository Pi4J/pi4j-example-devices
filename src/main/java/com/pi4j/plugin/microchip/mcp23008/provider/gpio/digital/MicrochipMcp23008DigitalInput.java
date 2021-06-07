package com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Microchip
 * FILENAME      :  MicrochipMcp23008DigitalInput.java
 * 
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import com.pi4j.io.gpio.digital.*;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;
/**
 * <p>MicrochipMcp23008DigitalInput class.</p>
 *
 * @version $Id: $Id
 */
public class MicrochipMcp23008DigitalInput extends DigitalInputBase implements DigitalInput {

    private DigitalState state = DigitalState.LOW;

    /**
     * <p>Constructor for MicrochipMcp23008DigitalInput.</p>
     *
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalInputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalInputConfig} object.
     */
    public MicrochipMcp23008DigitalInput(DigitalInputProvider provider, DigitalInputConfig config){
        super(provider, config);
    }

    public MicrochipMcp23008DigitalInput(DigitalInputProvider provider, DigitalInputConfig config, Boolean interruptEnabled){
        super(provider, config);
        this.interruptEnabled = interruptEnabled;
    }

    public MicrochipMcp23008DigitalInput(DigitalInputProvider provider, DigitalInputConfig config, Map<String,String> properties){
        super(provider, config);
        // load address property
        if(properties.containsKey(INTR_ENABLE_KEY)){
            var value = properties.get(INTR_ENABLE_KEY);
            if (value == "true") {
                this.interruptEnabled = true;
            }else{
                this.interruptEnabled = true;
            }
        } else {
            throw new ConfigMissingRequiredKeyException(INTR_ENABLE_KEY);
        }
     }

    // private configuration properties
    protected Boolean interruptEnabled = false;

    /** {@inheritDoc} */
    @Override
    public DigitalState state() {
        return this.state;
    }

    public Boolean interruptEnabled() {
        return this.interruptEnabled;
    };

    public String INTR_ENABLE_KEY = "true";
}
