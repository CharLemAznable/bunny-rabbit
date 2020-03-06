package com.github.charlemaznable.bunny.rabbittest.common;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BunnyHandlerElfTest {

    @Test
    public void testBunnyHandlerElf() {
        assertThrows(ReflectException.class, () ->
                onClass(BunnyHandlerElf.class).create().get());
    }
}
