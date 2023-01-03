package com.github.charlemaznable.bunny.rabbittest.spring.serve;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(VertxExtension.class)
@SpringJUnitConfig(ServeCallbackConfiguration.class)
public class ServeCallbackTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @BeforeAll
    public static void beforeAll() {
        ServeCallbackCommon.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        ServeCallbackCommon.afterAll();
    }

    @Test
    public void testServeCallbackEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeCallbackConfiguration.EVENT_BUS_DEPLOYED);
        ServeCallbackCommon.testServeCallbackEventBus(test, bunnyEventBus);
    }

    @Test
    public void testServeCallbackHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeCallbackConfiguration.HTTP_SERVER_DEPLOYED);
        ServeCallbackCommon.testServeCallbackHttpServer(test, vertx, bunnyOhClient);
    }
}
