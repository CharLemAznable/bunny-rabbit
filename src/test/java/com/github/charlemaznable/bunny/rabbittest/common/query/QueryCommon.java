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

    static final String CHARGING_TYPE_00 = "00";
    static final String CHARGING_TYPE_01 = "01";
    static final String CHARGING_TYPE_02 = "02";
    static final String CHARGING_TYPE_03 = "03";

    public static void testQueryEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, queryResponse.getChargingType());
                        assertEquals(0, queryResponse.getBalance());
                        assertEquals("条", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(CHARGING_TYPE_01, queryResponse.getChargingType());
                        assertEquals(100, queryResponse.getBalance());
                        assertEquals("MB", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertNull(queryResponse.getChargingType());
                        assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                        assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertNull(queryResponse.getChargingType());
                        assertEquals(UNEXPECTED_EXCEPTION.respCode(), queryResponse.getRespCode());
                        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Query Exception", queryResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testQueryHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_00, queryResponse.getChargingType());
                    assertEquals(0, queryResponse.getBalance());
                    assertEquals("条", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_01, queryResponse.getChargingType());
                    assertEquals(100, queryResponse.getBalance());
                    assertEquals("MB", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertNull(queryResponse.getChargingType());
                    assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                    assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertNull(queryResponse.getChargingType());
                    assertEquals(UNEXPECTED_EXCEPTION.respCode(), queryResponse.getRespCode());
                    assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Query Exception", queryResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
