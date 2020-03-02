package com.github.charlemaznable.bunny.rabbit.vertx.common;

import static com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Condition.notNullThen;

public final class BunnyElf {

    private BunnyElf() {
        throw new UnsupportedOperationException();
    }

    public static String failureMessage(Throwable failure) {
        if (failure instanceof BunnyException) {
            return failure.getMessage();
        } else if (null == failure) {
            return UNEXPECTED_EXCEPTION.message();
        } else if (null != failure.getMessage()) {
            return UNEXPECTED_EXCEPTION.message(failure.getMessage());
        } else {
            return UNEXPECTED_EXCEPTION.message(
                    notNullThen(failure.getCause(), Throwable::getMessage));
        }
    }
}
