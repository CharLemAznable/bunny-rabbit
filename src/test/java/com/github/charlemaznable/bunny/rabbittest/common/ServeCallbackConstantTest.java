package com.github.charlemaznable.bunny.rabbittest.common;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServeCallbackConstantTest {

    @Test
    public void testServeCallbackConstant() {
        assertThrows(ReflectException.class, () ->
                onClass(ServeCallbackConstant.class).create().get());
    }
}
