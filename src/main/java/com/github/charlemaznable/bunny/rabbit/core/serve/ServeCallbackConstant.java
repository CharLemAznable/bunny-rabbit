package com.github.charlemaznable.bunny.rabbit.core.serve;

public final class ServeCallbackConstant {

    public static final String CALLBACK_INITIAL = "0";
    public static final String CALLBACK_STANDBY = "1";
    public static final String CALLBACK_SUCCESS = "2";
    public static final String CALLBACK_FAILURE = "3";

    private ServeCallbackConstant() {
        throw new UnsupportedOperationException();
    }
}
