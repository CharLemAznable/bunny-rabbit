package com.github.charlemaznable.bunny.rabbittest.spring.callback;

import com.github.charlemaznable.bunny.rabbittest.common.callback.CallbackCommon;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = CallbackVerticleConfiguration.class)
public class CallbackVerticleTest {

    @BeforeAll
    public static void beforeAll() {
        CallbackCommon.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        CallbackCommon.afterAll();
    }

    @Test
    public void testCallbackVerticle(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CallbackVerticleConfiguration.CALLBACK_DEPLOYED);
        CallbackCommon.testCallbackVerticle(test);
    }
}
