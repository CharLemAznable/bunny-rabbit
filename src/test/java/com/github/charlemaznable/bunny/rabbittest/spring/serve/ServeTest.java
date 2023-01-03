package com.github.charlemaznable.bunny.rabbittest.spring.serve;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeServiceCommon;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(VertxExtension.class)
@SpringJUnitConfig(ServeConfiguration.class)
public class ServeTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testPreserveEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.EVENT_BUS_DEPLOYED);
        ServeCommon.testPreserveEventBus(test, bunnyEventBus);
    }

    @Test
    public void testPreserveHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.HTTP_SERVER_DEPLOYED);
        ServeCommon.testPreserveHttpServer(test, vertx, bunnyOhClient);
    }

    @Test
    public void testServeServiceEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.EVENT_BUS_DEPLOYED);
        ServeServiceCommon.testServeServiceEventBus(test, bunnyEventBus);
    }

    @Test
    public void testServeServiceHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.HTTP_SERVER_DEPLOYED);
        ServeServiceCommon.testServeServiceHttpServer(test, vertx, bunnyOhClient);
    }
}
