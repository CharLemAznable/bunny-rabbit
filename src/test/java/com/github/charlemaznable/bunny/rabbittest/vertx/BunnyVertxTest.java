package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.core.net.ohclient.OhReq;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = BunnyVertxConfiguration.class)
public class BunnyVertxTest {

    private static final String CHARGING_TYPE = "test";
    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testBunnyVertxEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> BunnyVertxConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val message = "";
                    vertx.eventBus().<String>request("/bunny/test", message, async -> test.verify(() -> {
                        val response = unJson(async.result().body(), TestResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val request = new TestRequest();
                    vertx.eventBus().<String>request("/bunny/test", json(request), async -> test.verify(() -> {
                        val response = unJson(async.result().body(), TestResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val request = new TestRequest();
                    request.setTestParameter("parameter");
                    bunnyEventBus.request(request, async -> test.verify(() -> {
                        val response = async.result();
                        assertTrue(response.isSuccess());
                        assertEquals("parameter:eventbus", response.getTestResult());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testBunnyVertxHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> BunnyVertxConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val result = new OhReq("http://127.0.0.1:12120/bunny/test")
                            .requestBody("").post();
                    val response = unJson(result, TestResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new TestRequest();
                    val result = new OhReq("http://127.0.0.1:12120/bunny/test")
                            .requestBody(json(request)).post();
                    val response = unJson(result, TestResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new TestRequest();
                    request.setTestParameter("parameter");
                    val response = bunnyOhClient.request(request);
                    assertTrue(response.isSuccess());
                    assertEquals("parameter:httpserver", response.getTestResult());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
