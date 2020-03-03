package com.github.charlemaznable.bunny.rabbittest.handler;

import com.github.charlemaznable.bunny.rabbit.handler.CalculateHandler;
import com.github.charlemaznable.bunny.rabbit.handler.ChargeHandler;
import com.github.charlemaznable.bunny.rabbit.handler.PaymentCommitHandler;
import com.github.charlemaznable.bunny.rabbit.handler.PaymentRollbackHandler;
import com.github.charlemaznable.bunny.rabbit.handler.QueryHandler;
import com.github.charlemaznable.bunny.rabbit.spring.CalculatePluginLoaderImpl;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConstructTest {

    @Test
    public void testCalculateHandler() {
        val calculatePluginLoader = new CalculatePluginLoaderImpl();
        val calculateHandler = new CalculateHandler(calculatePluginLoader);
        assertNotNull(on(calculateHandler).field("pluginNameMapper").get());
    }

    @Test
    public void testChargeHandler() {
        val chargeHandler = new ChargeHandler();
        assertNotNull(on(chargeHandler).field("bunnyDao").get());
    }

    @Test
    public void testPaymentCommitHandler() {
        val paymentCommitHandler = new PaymentCommitHandler();
        assertNotNull(on(paymentCommitHandler).field("bunnyDao").get());
    }

    @Test
    public void testPaymentRollbackHandler() {
        val paymentRollbackHandler = new PaymentRollbackHandler();
        assertNotNull(on(paymentRollbackHandler).field("bunnyDao").get());
    }

    @Test
    public void testQueryHandler() {
        val queryHandler = new QueryHandler();
        assertNotNull(on(queryHandler).field("bunnyDao").get());
    }
}
