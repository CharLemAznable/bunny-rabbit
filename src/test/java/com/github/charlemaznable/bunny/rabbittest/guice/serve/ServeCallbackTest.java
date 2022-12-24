package com.github.charlemaznable.bunny.rabbittest.guice.serve;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.guice.BunnyEventBusModular;
import com.github.charlemaznable.bunny.client.guice.BunnyOhClientModular;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbit.guice.BunnyModular;
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyVertxModular;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyLogDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyDaoServeImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
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
import static com.github.charlemaznable.configservice.diamond.DiamondFactory.getDiamond;
import static com.github.charlemaznable.core.spring.SpringFactory.springFactory;
import static com.github.charlemaznable.httpclient.ohclient.OhFactory.springOhLoader;
import static java.time.Duration.ofMillis;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.joor.Reflect.on;

@ExtendWith(VertxExtension.class)
public class ServeCallbackTest {

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
        MockDiamondServer.setConfigInfo("Bunny", "default", """
                httpserver.port=42121
                callback.delay=1000
                notfound.ServeCallback=NotFound
                """);
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:42121/bunny\n");

        val bunnyModular = new BunnyModular();
        bunnyModular.eqlerModuleBuilder().bind(BunnyLogDao.class, new BunnyLogDaoImpl())
                .bind(BunnyDao.class, BunnyDaoServeImpl.class)
                .bind(BunnyServeDao.class, BunnyServeDaoImpl.class)
                .bind(BunnyCallbackDao.class, BunnyCallbackDaoImpl.class);
        val nonsenseOptions = new NonsenseOptions();
        val signatureOptions = new SignatureOptions();
        val injector = Guice.createInjector(bunnyModular
                        .addCalculatePlugins()
                        .addServePlugins()
                        .addServeCallbackPlugins()
                        .addSwitchPlugins()
                        .scanPackageClasses(ServeCallbackCommon.class)
                        .chargeCodeMapper(getDiamond(ChargeCodeMapper.class))
                        .pluginNameMapper(getDiamond(PluginNameMapper.class))
                        .nonsenseOptions(nonsenseOptions)
                        .signatureOptions(signatureOptions)
                        .createModule(),
                new BunnyVertxModular().createModule(),
                new BunnyEventBusModular().nonsenseOptions(nonsenseOptions)
                        .signatureOptions(signatureOptions).createModule(),
                new BunnyOhClientModular().nonsenseOptions(nonsenseOptions)
                        .signatureOptions(signatureOptions).createModule());
        val application = injector.getInstance(BunnyVertxApplication.class);
        application.deploy(asyncResult -> {
            if (asyncResult.failed()) return;

            val deployment = asyncResult.result();
            if (EVENT_BUS_VERTICLE.equals(deployment.getVerticleName())) {
                ServeCallbackTest.EVENT_BUS_DEPLOYED = nonNull(deployment.getDeploymentId());
            } else if (HTTP_SERVER_VERTICLE.equals(deployment.getVerticleName())) {
                ServeCallbackTest.HTTP_SERVER_DEPLOYED = nonNull(deployment.getDeploymentId());
            }
        });
        vertx = injector.getInstance(Vertx.class);
        bunnyEventBus = injector.getInstance(BunnyEventBus.class);
        bunnyOhClient = injector.getInstance(BunnyOhClient.class);
        ServeCallbackCommon.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        ServeCallbackCommon.afterAll();
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testServeCallbackEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeCallbackTest.EVENT_BUS_DEPLOYED);
        ServeCallbackCommon.testServeCallbackEventBus(test, bunnyEventBus);
    }

    @Test
    public void testServeCallbackHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeCallbackTest.HTTP_SERVER_DEPLOYED);
        ServeCallbackCommon.testServeCallbackHttpServer(test, vertx, bunnyOhClient);
    }
}
