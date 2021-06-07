package com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Microchip
 * FILENAME      :  MicrochipMcp23008DigitalOutput.java
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
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;

/**
 * <p>MicrochipMcp23008DigitalOutput class.</p>
 *
  * @version $Id: $Id
 */
public class MicrochipMcp23008DigitalOutput extends DigitalOutputBase implements DigitalOutput {
    /**
     * <p>Constructor for MicrochipMcp23008DigitalOutput.</p>
     *
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalOutputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     */
    public MicrochipMcp23008DigitalOutput(DigitalOutputProvider provider, DigitalOutputConfig config){
        super(provider, config);
    }

    @Override
    public DigitalOutput on() throws IOException {
        return high();
    }

    @Override
    public DigitalOutput off() throws IOException {
        return low();
    }
}
