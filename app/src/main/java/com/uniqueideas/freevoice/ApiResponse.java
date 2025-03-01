package com.uniqueideas.freevoice;

public class ApiResponse {
    private boolean success;
    private String callSid;

    public boolean isSuccess() {
        return success;
    }

    public String getCallSid() {
        return callSid;
    }
}
