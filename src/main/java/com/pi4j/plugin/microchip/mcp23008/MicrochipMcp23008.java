package com.pi4j.plugin.microchip.mcp23008;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Microchip Mcp23008 I/O Providers
 * FILENAME      :  MicrochipMcp23008.java
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

import com.pi4j.io.gpio.digital.DigitalState;

/**
 * <p>Mcp23008 class.</p>
 *
 * @version $Id: $Id
 */
public class MicrochipMcp23008 {
    /** Constant <code>NAME="MicrochipMcp23008"</code> */
    public static final String NAME = "MicrochipMcp23008";
    /** Constant <code>ID="microchip-Mcp23008"</code> */
    public static final String ID = "microchip-mcp23008";

    // Digital Input (GPIO) Provider name and unique ID
    /** Constant <code>DIGITAL_INPUT_PROVIDER_NAME="NAME + Digital Input (GPIO) Provider"</code> */
    public static final String DIGITAL_INPUT_PROVIDER_NAME = NAME +  " Digital Input (GPIO) Provider";
    /** Constant <code>DIGITAL_INPUT_PROVIDER_ID="ID + -digital-input"</code> */
    public static final String DIGITAL_INPUT_PROVIDER_ID = ID + "-digital-input";

    // Digital Output (GPIO) Provider name and unique ID
    /** Constant <code>DIGITAL_OUTPUT_PROVIDER_NAME="NAME + Digital Output (GPIO) Provider"</code> */
    public static final String DIGITAL_OUTPUT_PROVIDER_NAME = NAME +  " Digital Output (GPIO) Provider";
    /** Constant <code>DIGITAL_OUTPUT_PROVIDER_ID="ID + -digital-output"</code> */
    public static final String DIGITAL_OUTPUT_PROVIDER_ID = ID + "-digital-output";




}
