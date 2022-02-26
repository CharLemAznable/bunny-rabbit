package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import lombok.NoArgsConstructor;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Condition.notNullThen;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BunnyElf {

    public static BunnyException failure(Throwable failure) {
        if (failure instanceof BunnyException) {
            return (BunnyException) failure;
        } else if (null == failure) {
            return UNEXPECTED_EXCEPTION.exception();
        } else if (null != failure.getMessage()) {
            return UNEXPECTED_EXCEPTION.exception(failure.getMessage());
        } else {
            return UNEXPECTED_EXCEPTION.exception(
                    notNullThen(failure.getCause(), Throwable::getMessage));
        }
    }

    public static String failureMessage(Throwable failure) {
        return failure(failure).getMessage();
    }
}
