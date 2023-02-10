package com.github.charlemaznable.bunny.rabbittest.guice.serve;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.guice.BunnyEventBusModular;
import com.github.charlemaznable.bunny.client.guice.BunnyOhClientModular;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.plugin.ServePlugin;
import com.github.charlemaznable.bunny.plugin.SwitchPlugin;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbit.guice.BunnyModular;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyVertxModular;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyLogDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyDaoServeImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCalculatePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeServiceCommon;
import com.github.charlemaznable.bunny.rabbittest.common.serve.TestServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbittest.common.serve.TestServePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.serve.TestSwitchPlugin;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
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
import static com.google.inject.name.Names.named;
import static java.time.Duration.ofMillis;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.joor.Reflect.on;

@ExtendWith(VertxExtension.class)
public class ServeTest {

    static boolean EVENT_BUS_DEPLOYED = false;
    static boolean HTTP_SERVER_DEPLOYED = false;
    private static Vertx vertx;
    private static BunnyEventBus bunnyEventBus;
    private static BunnyOhClient bunnyOhClient;

    @BeforeAll
    public static void beforeAll() {
        on(diamondLoader(springFactory())).field("configCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "default",
                "httpserver.port=42120\n" +
                        "notfound.Calculate=ServeCalculate\n" +
                        "notfound.Switch=TestSwitch\n" +
                        "notfound.Serve=NotFound\n" +
                        "NotFoundPlugin.Calculate=ServeCalculate\n" +
                        "NotFoundPlugin.Switch=TestSwitch\n");
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:42120/bunny\n");

        val injector = Guice.createInjector(
                new BunnyModular().bindDao(BunnyLogDao.class, new BunnyLogDaoImpl())
                        .bindDao(BunnyDao.class, BunnyDaoServeImpl.class)
                        .bindDao(BunnyServeDao.class, BunnyServeDaoImpl.class)
                        .bindDao(BunnyCallbackDao.class, BunnyCallbackDaoImpl.class).createModule(),
                new BunnyVertxModular().createModule(),
                new BunnyEventBusModular().createModule(),
                new BunnyOhClientModular().createModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(CalculatePlugin.class).annotatedWith(named("ServeCalculate")).to(ServeCalculatePlugin.class);
                        bind(ServeCallbackPlugin.class).annotatedWith(named("TestServeCallback")).to(TestServeCallbackPlugin.class);
                        bind(ServePlugin.class).annotatedWith(named("TestServe")).to(TestServePlugin.class);
                        bind(SwitchPlugin.class).annotatedWith(named("TestSwitch")).to(TestSwitchPlugin.class);
                    }
                });
        val application = injector.getInstance(BunnyVertxApplication.class);
        application.deploy(asyncResult -> {
            if (asyncResult.failed()) return;

            val deployment = asyncResult.result();
            if (EVENT_BUS_VERTICLE.equals(deployment.getVerticleName())) {
                ServeTest.EVENT_BUS_DEPLOYED = nonNull(deployment.getDeploymentId());
            } else if (HTTP_SERVER_VERTICLE.equals(deployment.getVerticleName())) {
                ServeTest.HTTP_SERVER_DEPLOYED = nonNull(deployment.getDeploymentId());
            }
        });
        vertx = injector.getInstance(Vertx.class);
        bunnyEventBus = injector.getInstance(BunnyEventBus.class);
        bunnyOhClient = injector.getInstance(BunnyOhClient.class);
    }

    @AfterAll
    public static void afterAll() {
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testPreserveEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeTest.EVENT_BUS_DEPLOYED);
        ServeCommon.testPreserveEventBus(test, bunnyEventBus);
    }

    @Test
    public void testPreserveHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeTest.HTTP_SERVER_DEPLOYED);
        ServeCommon.testPreserveHttpServer(test, vertx, bunnyOhClient);
    }

    @Test
    public void testServeServiceEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeTest.EVENT_BUS_DEPLOYED);
        ServeServiceCommon.testServeServiceEventBus(test, bunnyEventBus);
    }

    @Test
    public void testServeServiceHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeTest.HTTP_SERVER_DEPLOYED);
        ServeServiceCommon.testServeServiceHttpServer(test, vertx, bunnyOhClient);
    }
}
