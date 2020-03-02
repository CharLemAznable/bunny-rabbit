package com.github.charlemaznable.bunny.rabbittest.handler.config;

import com.github.charlemaznable.bunny.rabbit.handler.config.BunnyVertxConfiguration;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BunnyVertxConfigurationTest {

    @Test
    public void testBunnyVertxConfiguration() {
        val configuration = new BunnyVertxConfiguration();
        val vertxOptions = configuration.vertxOptions();
        assertEquals(64, vertxOptions.getWorkerPoolSize());
        assertEquals(60000000000L, vertxOptions.getMaxWorkerExecuteTime());
    }
}
