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
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.RESULT_1;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.RESULT_2;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.RESULT_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.calculate.TestCalculatePlugin.SUCCESS;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CalculateCommon {

    private static final String CALCULATE_SERVE_TYPE = "calculate";

    public static void testCalculateEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().putAll(of(RESULT_KEY, RESULT_2));
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS, RESULT_KEY, RESULT_1));
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertEquals(CALCULATE_SERVE_TYPE, calculateResponse.getServeName());
                        assertEquals(RESULT_1, calculateResponse.getCalculate());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getServeName());
                        assertEquals("TEST_CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Test Calculate Failed", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", calculateResponse.getRespCode());
                        assertEquals("Unexpected Exception: Calculate Error", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName("notfound");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getServeName());
                        assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Charge Calculate Failed: NotFound Plugin Not Found", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName("NotFoundPlugin");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getServeName());
                        assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Charge Calculate Failed: NotFoundPlugin.Calculate Plugin Not Found", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testCalculateHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().putAll(of(RESULT_KEY, RESULT_2));
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS, RESULT_KEY, RESULT_1));
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertEquals(CALCULATE_SERVE_TYPE, calculateResponse.getServeName());
                    assertEquals(RESULT_1, calculateResponse.getCalculate());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getServeName());
                    assertEquals("TEST_CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Test Calculate Failed", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName(CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", calculateResponse.getRespCode());
                    assertEquals("Unexpected Exception: Calculate Error", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName("notfound");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getServeName());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: NotFound Plugin Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setServeName("NotFoundPlugin");
                    calculateRequest.getContext().put(MtcpContext.TENANT_ID, CALCULATE_SERVE_TYPE);
                    calculateRequest.getContext().put(MtcpContext.TENANT_CODE, CALCULATE_SERVE_TYPE);
                    calculateRequest.setChargingParameters(newHashMap());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getServeName());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: NotFoundPlugin.Calculate Plugin Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
