package com.github.charlemaznable.bunny.rabbittest.spring.mtcp;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbittest.common.mtcp.MtcpCommon;
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
@ContextConfiguration(classes = MtcpConfiguration.class)
public class MtcpTest {

    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testMtcpEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> MtcpConfiguration.EVENT_BUS_DEPLOYED);
        MtcpCommon.testMtcpEventBus(test, bunnyEventBus);
    }

    @Test
    public void testMtcpHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> MtcpConfiguration.HTTP_SERVER_DEPLOYED);
        MtcpCommon.testMtcpHttpServer(test, vertx, bunnyOhClient);
    }
}
