package com.github.charlemaznable.bunny.rabbittest.illegal;

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

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = IllegalConfiguration.class)
public class IllegalTest {

    private static final String CHARGING_TYPE = "test";
    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testIllegalEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val message = "";
                    vertx.eventBus().<String>request("/bunny/illegal", message, async -> test.verify(() -> {
                        val response = unJson(async.result().body(), IllegalResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val request = new IllegalRequest();
                    vertx.eventBus().<String>request("/bunny/illegal", json(request), async -> test.verify(() -> {
                        val response = unJson(async.result().body(), IllegalResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testIllegalHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> IllegalConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val result = new OhReq("http://127.0.0.1:12225/bunny/illegal")
                            .requestBody("").post();
                    val response = unJson(result, IllegalResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new IllegalRequest();
                    val result = new OhReq("http://127.0.0.1:12225/bunny/illegal")
                            .requestBody(json(request)).post();
                    val response = unJson(result, IllegalResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
