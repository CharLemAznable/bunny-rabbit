package com.github.charlemaznable.bunny.rabbittest.spring.serve;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = ServeConfiguration.class)
public class ServeTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testServeEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.EVENT_BUS_DEPLOYED);
        ServeCommon.testServeEventBus(test, bunnyEventBus);
    }

    @Test
    public void testServeHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.HTTP_SERVER_DEPLOYED);
        ServeCommon.testServeHttpServer(test, vertx, bunnyOhClient);
    }
}
