package com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Microchip Mcp23008 I/O Providers
 * FILENAME      :  Mcp23008DigitalOutputProviderImpl.java
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

import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalOutput;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalOutputProvider;

/**
 * <p>MicrochipMcp23008DigitalOutputProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class MicrochipMcp23008DigitalOutputProviderImpl extends DigitalOutputProviderBase implements MicrochipMcp23008DigitalOutputProvider {

    /**
     * <p>Constructor for MicrochipMcp23008DigitalOutputProviderImpl.</p>
     */
    public MicrochipMcp23008DigitalOutputProviderImpl(){
        this.id = ID;
        this.name = NAME;
    }

    /** {@inheritDoc} */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {

        // validate provided address to ensure its an acceptable output for this chip
        // TODO :: I'm not sure if this the the right exception to throw or if this is the right location to perform address validation?
        if(config.address() < 1 || config.address() > 6)
            throw new IOException("Unsupported address for Microchip Mcp23008 digital output; (valid rage: 1-6)");

        // create an return new digital output instance
        return new MicrochipMcp23008DigitalOutput(this, config);
    }
}
