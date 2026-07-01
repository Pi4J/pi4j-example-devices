

package com.pi4j.devices.base_util.ffdc;

/**
 * FfdcLoggingModule
 * Interface declarations for custom logging, the implementing class
 * can add any desired information to the 'detail'.
 */

public interface FfdcLoggingModule {
    boolean ffdcMethodEntry(String detail);

    boolean ffdcMethodExit(String detail);

    boolean ffdcConfigWarningEntry(String detail);

    boolean ffdcDebugEntry(String detail);

    boolean ffdcErrorEntry(String detail);

    void ffdcErrorExit(String detail, int code);


    boolean ffdcClearLogs(String detail);

    boolean ffdcFlushShutdown();

}
