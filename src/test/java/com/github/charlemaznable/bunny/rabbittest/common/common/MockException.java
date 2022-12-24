package com.github.charlemaznable.bunny.rabbittest.common.common;

import java.io.Serial;

public class MockException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3547918025270399231L;

    public MockException(String msg) {
        super(msg);
    }
}
