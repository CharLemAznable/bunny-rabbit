package com.github.charlemaznable.bunny.rabbittest.common.mtcp;

import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MtcpCommon {

    public static void testMtcpEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val request = new MtcpRequest();
                    request.getExtend().put("tenantId", "tenantId");
                    request.getExtend().put("tenantCode", "tenantCode");
                    bunnyEventBus.request(request, async -> test.verify(() -> {
                        val response = async.result();
                        assertTrue(response.isSuccess());
                        assertEquals("tenantId:tenantCode", response.getContent());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testMtcpHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new MtcpRequest();
                    request.getExtend().put("tenantId", "tenantId");
                    request.getExtend().put("tenantCode", "tenantCode");
                    val response = bunnyOhClient.request(request);
                    assertTrue(response.isSuccess());
                    assertEquals("tenantId:tenantCode", response.getContent());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
