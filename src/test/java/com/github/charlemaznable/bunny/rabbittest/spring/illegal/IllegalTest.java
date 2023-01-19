package com.github.charlemaznable.bunny.rabbittest.spring.illegal;

import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalCommon;
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
@SpringJUnitConfig(IllegalConfiguration.class)
public class IllegalTest {

    @Autowired
    private Vertx vertx;

    @Test
    public void testIllegalEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalConfiguration.EVENT_BUS_DEPLOYED);
        IllegalCommon.testIllegalEventBus(test, vertx);
    }

    @Test
    public void testIllegalHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalConfiguration.HTTP_SERVER_DEPLOYED);
        IllegalCommon.testIllegalHttpServer(test, vertx, 32117);
    }
}
