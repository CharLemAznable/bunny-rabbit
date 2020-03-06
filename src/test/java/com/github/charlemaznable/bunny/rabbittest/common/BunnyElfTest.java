package com.github.charlemaznable.bunny.rabbittest.common;

import com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf;
import lombok.val;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BunnyElfTest {

    @Test
    public void testBunnyElf() {
        assertThrows(ReflectException.class, () ->
                onClass(BunnyElf.class).create().get());

        val nullFailure = BunnyElf.failure(null);
        assertEquals(UNEXPECTED_EXCEPTION.respCode(), nullFailure.respCode());
        assertEquals(UNEXPECTED_EXCEPTION.respDesc(), nullFailure.respDesc());

        val runtimeException = new RuntimeException();
        val runtimeFailure = BunnyElf.failure(runtimeException);
        assertEquals(UNEXPECTED_EXCEPTION.respCode(), runtimeFailure.respCode());
        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": ", runtimeFailure.respDesc());
    }
}
