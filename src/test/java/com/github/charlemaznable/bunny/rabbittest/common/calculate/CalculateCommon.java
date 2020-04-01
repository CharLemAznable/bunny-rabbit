package com.github.charlemaznable.bunny.rabbittest.common.calculate;

import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;

import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.CALCULATE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.RESULT;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.RESULT_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.SUCCESS;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.UNIT;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.UNIT_KEY;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CalculateCommon {

    private static final String CHARGING_TYPE = "test";

    public static void testCalculateEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.getContext().putAll(of(RESULT_KEY, RESULT, UNIT_KEY, UNIT));
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertEquals(CHARGING_TYPE, calculateResponse.getChargingType());
                        assertEquals(RESULT, calculateResponse.getCalculate());
                        assertEquals(UNIT, calculateResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getChargingType());
                        assertEquals("TEST_CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Test Calculate Failed", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getChargingType());
                        assertEquals("UNEXPECTED_EXCEPTION", calculateResponse.getRespCode());
                        assertEquals("Unexpected Exception: Calculate Error", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("notfound");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getChargingType());
                        assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Charge Calculate Failed: NotFound Plugin Not Found", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("NotFoundPlugin");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getChargingType());
                        assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Charge Calculate Failed: Calculate.NotFoundPlugin Plugin Not Found", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testCalculateHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.getContext().putAll(of(RESULT_KEY, RESULT, UNIT_KEY, UNIT));
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertEquals(CHARGING_TYPE, calculateResponse.getChargingType());
                    assertEquals(RESULT, calculateResponse.getCalculate());
                    assertEquals(UNIT, calculateResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("TEST_CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Test Calculate Failed", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", calculateResponse.getRespCode());
                    assertEquals("Unexpected Exception: Calculate Error", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("notfound");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: NotFound Plugin Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("NotFoundPlugin");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: Calculate.NotFoundPlugin Plugin Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
