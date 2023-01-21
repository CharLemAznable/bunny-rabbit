package com.github.charlemaznable.bunny.rabbittest.guice.illegal;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.guice.BunnyModular;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyVertxModular;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyLogDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalCommon;
import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalConfig;
import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalHandler;
import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalVertxConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.ProvidesIntoSet;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.bunny.rabbit.core.verticle.EventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.core.verticle.HttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.configservice.diamond.DiamondFactory.diamondLoader;
import static com.github.charlemaznable.core.spring.SpringFactory.springFactory;
import static com.github.charlemaznable.httpclient.ohclient.OhFactory.springOhLoader;
import static java.time.Duration.ofMillis;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.joor.Reflect.on;

@ExtendWith(VertxExtension.class)
public class IllegalTest {

    static boolean EVENT_BUS_DEPLOYED = false;
    static boolean HTTP_SERVER_DEPLOYED = false;
    private static Vertx vertx;

    @BeforeAll
    public static void beforeAll() {
        on(diamondLoader(springFactory())).field("configCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "illegal", """
                httpserver.port=42117
                httpserver.context-path=
                eventbus.address-prefix=
                """);

        val injector = Guice.createInjector(
                new BunnyModular(IllegalConfig.class).bindDao(BunnyLogDao.class, new BunnyLogDaoImpl()).createModule(),
                new BunnyVertxModular(IllegalVertxConfig.class).createModule(),
                new AbstractModule() {
                    @SuppressWarnings("rawtypes")
                    @ProvidesIntoSet
                    public BunnyHandler illegalHandler() {
                        return new IllegalHandler();
                    }
                });
        val application = injector.getInstance(BunnyVertxApplication.class);
        application.deploy(asyncResult -> {
            if (asyncResult.failed()) return;

            val deployment = asyncResult.result();
            if (EVENT_BUS_VERTICLE.equals(deployment.getVerticleName())) {
                IllegalTest.EVENT_BUS_DEPLOYED = nonNull(deployment.getDeploymentId());
            } else if (HTTP_SERVER_VERTICLE.equals(deployment.getVerticleName())) {
                IllegalTest.HTTP_SERVER_DEPLOYED = nonNull(deployment.getDeploymentId());
            }
        });
        vertx = injector.getInstance(Vertx.class);
    }

    @AfterAll
    public static void afterAll() {
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testIllegalEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalTest.EVENT_BUS_DEPLOYED);
        IllegalCommon.testIllegalEventBus(test, vertx);
    }

    @Test
    public void testIllegalHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalTest.HTTP_SERVER_DEPLOYED);
        IllegalCommon.testIllegalHttpServer(test, vertx, 42117);
    }
}
