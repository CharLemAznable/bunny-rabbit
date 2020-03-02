package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyApplication;
import com.github.charlemaznable.core.net.ohclient.OhReq;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.miner.MinerFactory.springMinerLoader;
import static com.github.charlemaznable.core.net.ohclient.OhFactory.getClient;
import static com.github.charlemaznable.core.net.ohclient.OhFactory.springOhLoader;
import static java.time.Duration.ofMillis;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.joor.Reflect.on;

@ExtendWith(VertxExtension.class)
public class BunnyVertxTest {

    private static final String CHARGING_TYPE = "test";
    private static Vertx vertx;
    private static boolean deployedEventBus;
    private static boolean deployedHttpServer;
    private static BunnyEventBus bunnyEventBus;
    private static BunnyOhClient bunnyOhClient;

    @BeforeAll
    public static void beforeAll() {
        on(springMinerLoader()).field("minerCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "default",
                "httpserver.port=12120");
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:12120/bunny");

        vertx = Vertx.vertx();
        val testHandler = new TestHandler();
        new BunnyApplication(testHandler).deploy(vertx, arDeployment -> {
            if (arDeployment.failed()) return;

            val deployment = arDeployment.result();
            if (EVENT_BUS_VERTICLE.equals(deployment.getVerticleName())) {
                deployedEventBus = nonNull(deployment.getDeploymentId());
            } else if (HTTP_SERVER_VERTICLE.equals(deployment.getVerticleName())) {
                deployedHttpServer = nonNull(deployment.getDeploymentId());
            }
        });

        bunnyEventBus = new BunnyEventBus(vertx);
        bunnyOhClient = getClient(BunnyOhClient.class);
    }

    @AfterAll
    public static void afterAll() {
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testBunnyVertxEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> BunnyVertxTest.deployedEventBus);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val message = "";
                    vertx.eventBus().<String>request("/bunny/test", message, async -> test.verify(() -> {
                        val response = unJson(async.result().body(), TestResponse.class);
                        Assertions.assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        Assertions.assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val request = new TestRequest();
                    vertx.eventBus().<String>request("/bunny/test", json(request), async -> test.verify(() -> {
                        val response = unJson(async.result().body(), TestResponse.class);
                        Assertions.assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        Assertions.assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testBunnyVertxHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> BunnyVertxTest.deployedHttpServer);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val result = new OhReq("http://127.0.0.1:12120/bunny/test")
                            .requestBody("").post();
                    val response = unJson(result, TestResponse.class);
                    Assertions.assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    Assertions.assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new TestRequest();
                    val result = new OhReq("http://127.0.0.1:12120/bunny/test")
                            .requestBody(json(request)).post();
                    val response = unJson(result, TestResponse.class);
                    Assertions.assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    Assertions.assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
