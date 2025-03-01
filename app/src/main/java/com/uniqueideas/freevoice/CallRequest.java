package com.uniqueideas.freevoice;

public class CallRequest {
    private String caller;
    private String receiver;

    public CallRequest(String caller, String receiver) {
        this.caller = caller;
        this.receiver = receiver;
    }

    public String getCaller() {
        return caller;
    }

    public String getReceiver() {
        return receiver;
    }
}
