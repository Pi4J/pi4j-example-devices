#
#
#      *
#      * -
#      * #%L
#      * **********************************************************************
#      * ORGANIZATION  :  Pi4J
#      * PROJECT       :  Pi4J :: EXTENSION
#      * FILENAME      : README.md
#      *
#      * This file is part of the Pi4J project. More information about
#      * this project can be found here:  https://pi4j.com/
#      * **********************************************************************
#      * %%
#      *   * Copyright (C) 2012 - 2023 Pi4J
#       * %%
#      *
#      * Licensed under the Apache License, Version 2.0 (the "License");
#      * you may not use this file except in compliance with the License.
#      * You may obtain a copy of the License at
#      *
#      *      http://www.apache.org/licenses/LICENSE-2.0
#      *
#      * Unless required by applicable law or agreed to in writing, software
#      * distributed under the License is distributed on an "AS IS" BASIS,
#      * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#      * See the License for the specific language governing permissions and
#      * limitations under the License.
#      * #L%
#      *
#
#
          _        ______   _______  _______                                                                
|\     /|( (    /|(  __  \ (  ____ \(  ____ )                                                               
| )   ( ||  \  ( || (  \  )| (    \/| (    )|                                                               
| |   | ||   \ | || |   ) || (__    | (____)|                                                               
| |   | || (\ \) || |   | ||  __)   |     __)                                                               
| |   | || | \   || |   ) || (      | (\ (                                                                  
| (___) || )  \  || (__/  )| (____/\| ) \ \__                                                               
(_______)|/    )_)(______/ (_______/|/   \__/

 _______  _______  _        _______ _________ _______           _______ __________________ _______  _       
(  ____ \(  ___  )( (    /|(  ____ \\__   __/(  ____ )|\     /|(  ____ \\__   __/\__   __/(  ___  )( (    /|
| (    \/| (   ) ||  \  ( || (    \/   ) (   | (    )|| )   ( || (    \/   ) (      ) (   | (   ) ||  \  ( |
| |      | |   | ||   \ | || (_____    | |   | (____)|| |   | || |         | |      | |   | |   | ||   \ | |
| |      | |   | || (\ \) |(_____  )   | |   |     __)| |   | || |         | |      | |   | |   | || (\ \) |
| |      | |   | || | \   |      ) |   | |   | (\ (   | |   | || |         | |      | |   | |   | || | \   |
| (____/\| (___) || )  \  |/\____) |   | |   | ) \ \__| (___) || (____/\   | |   ___) (___| (___) || )  \  |
(_______/(_______)|/    )_)\_______)   )_(   |/   \__/(_______)(_______/   )_(   \_______/(_______)|/    )_)

# NeoPixel94V RGB Stick

At present this code attempts to use SPI and GPIO bit banging.
Neither work correctly as they cannot maintain the architected 
pulse duration to represent a one nor a zero.
int32_t highTime0NanoSeconds,    400 ns
int32_t lowTime0NanoSeconds,     850 ns
int32_t highTime1NanoSeconds,    800 ns
int32_t lowTime1NanoSeconds,     450 ns

# So I am integrating this code simply to protect it from loss.


Composition of 24bit data:
G7 G6 G5 G4 G3 G2 G1 G0 R7 R6 R5 R4 R3 R2 R1 R0 B7 B6 B5 B4 B3 B2 B1 B0
Note: Follow the order of GRB to sent data and the high bit sent at first.

The NeoPixel94V class assumes the above order Green Red Blue.  The code 
method Render() uses this assumption when inflating the LEDs integer bits 
to individual bytes.

If your LED strip uses a different order within the 24 bits the render function 
must be modified.

