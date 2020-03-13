package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static com.github.charlemaznable.core.lang.Str.toStr;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum BunnyError {

    REQUEST_BODY_ERROR("REQUEST_BODY_ERROR", "Request Body Error"),

    CALCULATE_FAILED("CALCULATE_FAILED", "Charge Calculate Failed"),

    CHARGE_FAILED("CHARGE_FAILED", "Account Charge Failed"),

    QUERY_FAILED("QUERY_FAILED", "Balance Query Failed"),

    SERVE_FAILED("SERVE_FAILED", "Serve Failed"),

    PRE_SERVE_FAILED("PRE_SERVE_FAILED", "Pre-Serve Failed"),

    CONFIRM_FAILED("CONFIRM_FAILED", "Serve Confirm Failed"),

    SERVE_CALLBACK_FAILED("SERVE_CALLBACK_FAILED", "Serve Callback Failed"),

    UNEXPECTED_EXCEPTION("UNEXPECTED_EXCEPTION", "Unexpected Exception");

    private final String respCode;
    private final String respDesc;

    public BunnyException exception() {
        return new BunnyException(respCode, respDesc);
    }

    public BunnyException exception(String moreDesc) {
        return new BunnyException(respCode, respDesc + ": " + toStr(moreDesc));
    }
}
