package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.core.net.ohclient.OhReq;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;

import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IllegalCommon {

    public static void testIllegalEventBus(VertxTestContext test, Vertx vertx) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val message = "";
                    vertx.eventBus().<String>request("/illegal", message, async -> test.verify(() -> {
                        val response = unJson(async.result().body(), IllegalResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val request = new IllegalRequest();
                    vertx.eventBus().<String>request("/illegal", json(request), async -> test.verify(() -> {
                        val response = unJson(async.result().body(), IllegalResponse.class);
                        assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                        assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                        f.complete();
                    }));
                })
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testIllegalHttpServer(VertxTestContext test, Vertx vertx, int port) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val result = new OhReq("http://127.0.0.1:" + port + "/illegal")
                            .requestBody("").post();
                    val response = unJson(result, IllegalResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Body is Blank", response.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new IllegalRequest();
                    val result = new OhReq("http://127.0.0.1:" + port + "/illegal")
                            .requestBody(json(request)).post();
                    val response = unJson(result, IllegalResponse.class);
                    assertEquals("REQUEST_BODY_ERROR", response.getRespCode());
                    assertEquals("Request Body Error: Verify Failed", response.getRespDesc());
                    p.complete();
                }, false, f))
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }
}
