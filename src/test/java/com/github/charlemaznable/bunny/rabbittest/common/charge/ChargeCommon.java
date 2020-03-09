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

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CHARGE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChargeCommon {

    static final String CHARGING_TYPE_00 = "00";
    static final String CHARGING_TYPE_01 = "01";
    static final String CHARGING_TYPE_02 = "02";
    static final String CHARGING_TYPE_03 = "03";

    public static void testChargeEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_00);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, chargeResponse.getChargingType());
                        assertTrue(chargeResponse.isSuccess());
                        val queryRequest = new QueryRequest();
                        queryRequest.setChargingType(CHARGING_TYPE_00);
                        bunnyEventBus.request(queryRequest, async2 -> test.verify(() -> {
                            val queryResponse = async2.result();
                            assertEquals(CHARGING_TYPE_00, queryResponse.getChargingType());
                            assertEquals(100, queryResponse.getBalance());
                            assertEquals("条", queryResponse.getUnit());
                            f.complete();
                        }));
                    }));
                }),
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_02);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertNull(chargeResponse.getChargingType());
                        assertEquals(CHARGE_FAILED.respCode(), chargeResponse.getRespCode());
                        assertEquals(CHARGE_FAILED.respDesc(), chargeResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_03);
                    chargeRequest.setChargeValue(100);
                    bunnyEventBus.request(chargeRequest, async -> test.verify(() -> {
                        val chargeResponse = async.result();
                        assertNull(chargeResponse.getChargingType());
                        assertEquals(UNEXPECTED_EXCEPTION.respCode(), chargeResponse.getRespCode());
                        assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Charge Exception", chargeResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testChargeHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_01);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertEquals(CHARGING_TYPE_01, chargeResponse.getChargingType());
                    assertTrue(chargeResponse.isSuccess());
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_01);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_01, queryResponse.getChargingType());
                    assertEquals(200, queryResponse.getBalance());
                    assertEquals("MB", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_02);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertNull(chargeResponse.getChargingType());
                    assertEquals(CHARGE_FAILED.respCode(), chargeResponse.getRespCode());
                    assertEquals(CHARGE_FAILED.respDesc(), chargeResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val chargeRequest = new ChargeRequest();
                    chargeRequest.setChargingType(CHARGING_TYPE_03);
                    chargeRequest.setChargeValue(100);
                    val chargeResponse = bunnyOhClient.request(chargeRequest);
                    assertNull(chargeResponse.getChargingType());
                    assertEquals(UNEXPECTED_EXCEPTION.respCode(), chargeResponse.getRespCode());
                    assertEquals(UNEXPECTED_EXCEPTION.respDesc() + ": Charge Exception", chargeResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
