package com.github.charlemaznable.bunny.rabbittest.guice.callback;

import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.guice.BunnyModular;
import com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.callback.BunnyCallbackDaoImpl;
import com.github.charlemaznable.bunny.rabbittest.common.callback.CallbackCommon;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyLogDaoImpl;
import com.google.inject.Guice;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.bunny.rabbit.core.verticle.CallbackVerticle.CALLBACK_VERTICLE;
import static com.github.charlemaznable.core.miner.MinerFactory.springMinerLoader;
import static com.github.charlemaznable.core.net.ohclient.OhFactory.springOhLoader;
import static java.time.Duration.ofMillis;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.joor.Reflect.on;

@ExtendWith(VertxExtension.class)
public class CallbackVerticleTest {

    static boolean CALLBACK_DEPLOYED = false;

    @BeforeAll
    public static void beforeAll() {
        on(springMinerLoader()).field("minerCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "default",
                "httpserver.port=42115\n" +
                        "callback.delay=1000");
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:42115/bunny\n");

        val bunnyModular = new BunnyModular();
        bunnyModular.eqlerModuleBuilder().bind(BunnyLogDao.class, new BunnyLogDaoImpl())
                .bind(BunnyCallbackDao.class, BunnyCallbackDaoImpl.class);
        bunnyModular.addCalculatePlugins(TestCalculatePlugin.class);
        val injector = Guice.createInjector(bunnyModular.createModule());
        val application = injector.getInstance(BunnyVertxApplication.class);
        application.deploy(asyncResult -> {
            if (asyncResult.failed()) return;

            val deployment = asyncResult.result();
            if (CALLBACK_VERTICLE.equals(deployment.getVerticleName())) {
                CallbackVerticleTest.CALLBACK_DEPLOYED = nonNull(deployment.getDeploymentId());
            }
        });
        CallbackCommon.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        CallbackCommon.afterAll();
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testCallbackVerticle(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CallbackVerticleTest.CALLBACK_DEPLOYED);
        CallbackCommon.testCallbackVerticle(test);
    }
}
