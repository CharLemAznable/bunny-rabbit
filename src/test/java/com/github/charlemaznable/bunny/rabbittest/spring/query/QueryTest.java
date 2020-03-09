package com.github.charlemaznable.bunny.rabbittest.spring.query;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.query.QueryCommon;
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
@ContextConfiguration(classes = QueryConfiguration.class)
public class QueryTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testQueryEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> QueryConfiguration.EVENT_BUS_DEPLOYED);
        QueryCommon.testQueryEventBus(test, bunnyEventBus);
    }

    @Test
    public void testQueryHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> QueryConfiguration.HTTP_SERVER_DEPLOYED);
        QueryCommon.testQueryHttpServer(test, vertx, bunnyOhClient);
    }
}
