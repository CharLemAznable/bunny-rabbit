package com.github.charlemaznable.bunny.rabbittest.common.query;

import com.github.charlemaznable.bunny.client.domain.QueryRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.QUERY_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class QueryCommon {

    private static final String SERVE_NAME_00 = "00";
    private static final String SERVE_NAME_01 = "01";
    private static final String SERVE_NAME_02 = "02";
    private static final String SERVE_NAME_03 = "03";

    public static void testQueryEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_00);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(SERVE_NAME_00, queryResponse.getServeName());
                        assertEquals(0, queryResponse.getBalance());
                        assertEquals("条", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_01);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(SERVE_NAME_01, queryResponse.getServeName());
                        assertEquals(100, queryResponse.getBalance());
                        assertEquals("MB", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_02);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertNull(queryResponse.getServeName());
                        assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                        assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_03);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertNull(queryResponse.getServeName());
                        assertEquals(UNEXPECTED_EXCEPTION.respCode(), queryResponse.getRespCode());
                        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Query Exception", queryResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).onComplete(event -> test.<CompositeFuture>succeedingThenComplete().handle(event));
    }

    public static void testQueryHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_00);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(SERVE_NAME_00, queryResponse.getServeName());
                    assertEquals(0, queryResponse.getBalance());
                    assertEquals("条", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_01);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(SERVE_NAME_01, queryResponse.getServeName());
                    assertEquals(100, queryResponse.getBalance());
                    assertEquals("MB", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_02);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertNull(queryResponse.getServeName());
                    assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                    assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_03);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertNull(queryResponse.getServeName());
                    assertEquals(UNEXPECTED_EXCEPTION.respCode(), queryResponse.getRespCode());
                    assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Query Exception", queryResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).onComplete(event -> test.<CompositeFuture>succeedingThenComplete().handle(event));
    }
}
