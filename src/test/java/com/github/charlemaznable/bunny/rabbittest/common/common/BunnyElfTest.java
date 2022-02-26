package com.github.charlemaznable.bunny.rabbittest.common.common;

import com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BunnyElfTest {

    @Test
    public void testBunnyElf() {
        val nullFailure = BunnyElf.failure(null);
        assertEquals(UNEXPECTED_EXCEPTION.respCode(), nullFailure.respCode());
        assertEquals(UNEXPECTED_EXCEPTION.respDesc(), nullFailure.respDesc());

        val runtimeException = new RuntimeException();
        val runtimeFailure = BunnyElf.failure(runtimeException);
        assertEquals(UNEXPECTED_EXCEPTION.respCode(), runtimeFailure.respCode());
        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": ", runtimeFailure.respDesc());
    }
}
