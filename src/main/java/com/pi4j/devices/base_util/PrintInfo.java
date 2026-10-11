

package com.pi4j.devices.base_util;


import com.pi4j.context.Context;
import com.pi4j.registry.Registry;
import com.pi4j.util.Console;

/**
 * Helper class to output info about the Pi4J {@link Context}.
 * <p>
 * After we initialize Pi4J, we can access the following core parts of the system:
 * <ul>
 *     <li>Platforms</li>
 *     <li>Platform (Default Runtime Platform)</li>
 *     <li>Providers (I/O Providers)</li>
 *     <li>Registry (I/O Registry)</li>
 * </ul>
 * <p>
 *     Details and implementation copied from pi4j_V2 minimal example.
 * </p>
 */
public class PrintInfo {



    /**
     * This method used to print Pi4J providers, which are no longer supported
     * starting from V5.0.
     *
     * @param console {@link Console}
     * @param pi4j    {@link Context}
     * @deprecated
     */
    @Deprecated(since = "5.0")
    public static void printProviders(Console console, Context pi4j) {
        console.println("(Pi4J Providers are no longer used since Pi4J 5.0)");
        console.println();
    }

    /**
     * Pi4J Registry
     * <p>The registry stores the state of all the I/O managed by Pi4J.</p>
     *
     * @param console {@link Console}
     * @param pi4j    {@link Context}
     */
    public static void printRegistry(Console console, Context pi4j) {
        Registry registry = pi4j.registry();

        // Let's print out to the console the detected and loaded
        // I/O interfaces registered with Pi4J and included in the 'Registry'.
        console.box("Pi4J REGISTRY");
        console.println();
        registry.describe().print(System.out);
        console.println();
    }
}
