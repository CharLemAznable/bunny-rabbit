package com.github.charlemaznable.bunny.rabbit.core.serve;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ServeCallbackConstant {

    public static final String CALLBACK_STANDBY = "0";
    public static final String CALLBACK_SUCCESS = "1";
    public static final String CALLBACK_FAILURE = "2";
}
