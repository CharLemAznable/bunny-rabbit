package com.github.charlemaznable.bunny.rabbit.vertx.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BunnyError {

    REQUEST_BODY_ERROR("REQUEST_BODY_ERROR", "Request Body Error"),

    UNEXPECTED_EXCEPTION("UNEXPECTED_EXCEPTION", "Unexpected Exception");

    private final String respCode;
    private final String respDesc;

    public BunnyException exception() {
        return new BunnyException(respCode, respDesc);
    }

    public BunnyException exception(String moreDesc) {
        return new BunnyException(respCode, respDesc + ": " + moreDesc);
    }

    public String message() {
        return exception().getMessage();
    }

    public String message(String moreDesc) {
        return exception(moreDesc).getMessage();
    }
}
