package com.github.charlemaznable.bunny.rabbittest.spring.calculate;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.calculate.CalculateCommon;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;

@ExtendWith(VertxExtension.class)
@SpringJUnitConfig(CalculateConfiguration.class)
public class CalculateTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testCalculateEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CalculateConfiguration.EVENT_BUS_DEPLOYED);
        CalculateCommon.testCalculateEventBus(test, bunnyEventBus);
    }

    @Test
    public void testCalculateHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CalculateConfiguration.HTTP_SERVER_DEPLOYED);
        CalculateCommon.testCalculateHttpServer(test, vertx, bunnyOhClient);
    }
}
