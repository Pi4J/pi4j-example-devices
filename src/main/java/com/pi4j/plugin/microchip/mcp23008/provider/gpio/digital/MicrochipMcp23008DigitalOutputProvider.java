package com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Microchip
 * FILENAME      :  MicrochipMcp23008DigitalOutputProvider.java
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

import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.plugin.microchip.mcp23008.MicrochipMcp23008;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.impl.MicrochipMcp23008DigitalOutputProviderImpl;

/**
 * <p>MicrochipMcp23008DigitalOutputProvider interface.</p>
 *
 * @version $Id: $Id
 */
public interface MicrochipMcp23008DigitalOutputProvider extends DigitalOutputProvider {

    /** Constant <code>NAME="MicrochipMcp23008.DIGITAL_OUTPUT_PROVIDER_NAME"</code> */
    String NAME = MicrochipMcp23008.DIGITAL_OUTPUT_PROVIDER_NAME;

    /** Constant <code>ID="MicrochipMcp23008.DIGITAL_OUTPUT_PROVIDER_ID"</code> */
    String ID = MicrochipMcp23008.DIGITAL_OUTPUT_PROVIDER_ID;

    /**
     * <p>newInstance.</p>
     *
     * @return a {@link MicrochipMcp23008DigitalOutputProvider} object.
     */
    static MicrochipMcp23008DigitalOutputProvider newInstance() {
        return new MicrochipMcp23008DigitalOutputProviderImpl();
    }
}
