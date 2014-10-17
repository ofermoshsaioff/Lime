package com.moshaioff.lime.tasks;

/**
 * Created by ofer on 10/9/14.
 */
public class NdefWriteResult {

    public NdefWriteResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    private boolean success;
    private String msg;

    public String getMessage() {
        return msg;
    }

    public boolean isSuccess() {
        return success;
    }

}
