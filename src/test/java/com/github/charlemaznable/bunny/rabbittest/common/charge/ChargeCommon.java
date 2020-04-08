package com.github.charlemaznable.bunny.rabbittest.common.charge;

import com.github.charlemaznable.bunny.client.domain.ChargeRequest;
import com.github.charlemaznable.bunny.client.domain.QueryRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CHARGE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChargeCommon {

    private static final String SERVE_NAME_00 = "00";
    private static final String SERVE_NAME_01 = "01";
    private static final String SERVE_NAME_02 = "02";
    private static final String SERVE_NAME_03 = "03";

    public static void testChargeEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_00);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_00);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_00);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertEquals(SERVE_NAME_00, chargeResponse.getServeName());
                        assertTrue(chargeResponse.isSuccess());
                        val queryRequest = new QueryRequest();
                        queryRequest.setServeName(SERVE_NAME_00);
                        queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_00);
                        queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_00);
                        bunnyEventBus.request(queryRequest, async2 -> test.verify(() -> {
                            val queryResponse = async2.result();
                            assertEquals(SERVE_NAME_00, queryResponse.getServeName());
                            assertEquals(100, queryResponse.getBalance());
                            assertEquals("条", queryResponse.getUnit());
                            f.complete();
                        }));
                    }));
                }),
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_02);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_02);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_02);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertNull(chargeResponse.getServeName());
                        assertEquals(CHARGE_FAILED.respCode(), chargeResponse.getRespCode());
                        assertEquals(CHARGE_FAILED.respDesc(), chargeResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_03);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_03);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_03);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertNull(chargeResponse.getServeName());
                        assertEquals(UNEXPECTED_EXCEPTION.respCode(), chargeResponse.getRespCode());
                        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Charge Exception", chargeResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testChargeHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_01);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_01);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_01);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertEquals(SERVE_NAME_01, chargeResponse.getServeName());
                    assertTrue(chargeResponse.isSuccess());
                    val queryRequest = new QueryRequest();
                    queryRequest.setServeName(SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_01);
                    queryRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_01);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(SERVE_NAME_01, queryResponse.getServeName());
                    assertEquals(200, queryResponse.getBalance());
                    assertEquals("MB", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_02);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_02);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_02);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertNull(chargeResponse.getServeName());
                    assertEquals(CHARGE_FAILED.respCode(), chargeResponse.getRespCode());
                    assertEquals(CHARGE_FAILED.respDesc(), chargeResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setServeName(SERVE_NAME_03);
                    chargeRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME_03);
                    chargeRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME_03);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertNull(chargeResponse.getServeName());
                    assertEquals(UNEXPECTED_EXCEPTION.respCode(), chargeResponse.getRespCode());
                    assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Charge Exception", chargeResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }
}
