package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.ServeRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;

import java.util.Map;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CONFIRM_FAILED;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_00;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_01;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_02;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_03;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_04;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_05;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_06;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_07;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_08;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeCommon {

    static final String CALCULATE_KEY = "CALC";
    static final String SERVE_KEY = "SERVE";
    static final String SERVE_CHECK_KEY = "SERVE_CHECK";
    static final String SERVE_SWITCH_KEY = "SERVE_SWITCH";
    static final String SERVE_SWITCH_CONFIRM_KEY = "SERVE_SWITCH_CONFIRM";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";
    static final String UNDEFINED = "UNDEFINED";
    static final String SWITCH_ERROR = "SWITCH_ERROR";

    public static void testServeEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Calculate Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("SERVE_CALCULATE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Calculate Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(2);
                    serveRequest.setServeType("any");
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Pre-Serve Failed: Balance Deduct Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(3);
                    serveRequest.setServeType("any");
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Pre-Serve Failed: Sequence Create Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(4);
                    serveRequest.setServeType("any");
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Exception", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("notfound");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Failed: NotFound Plugin Not Found", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("NotFoundPlugin");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Failed: Serve.NotFoundPlugin Plugin Not Found", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS, SERVE_KEY, SERVE_KEY));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_01);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_02);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Confirm Failed: Sequence Confirm Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_03);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Confirm Failed: Balance Confirm Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_04);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_04);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_04);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Unexpected Exception: Serve Confirm Exception)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Check Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                eventBusServeCheckFuture(test, bunnyEventBus, FAILURE),
                eventBusServeCheckFuture(test, bunnyEventBus, UNDEFINED),
                eventBusServeCheckFuture(test, bunnyEventBus, SUCCESS),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_05);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_05);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_05);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_05, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertTrue(serveResponse.isSuccess());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_06);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_06);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_06);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_06, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_07);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_07);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_07);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_07, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_08);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_08);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_08);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_08, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals("Serve Confirm Exception", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("TEST_SERVE_SWITCH_FAILED", serveResponse.getRespCode());
                        assertEquals("Test Serve Switch Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SWITCH_ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Switch Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                        assertEquals("{\"respDesc\":\"Test Serve Switch Failed\",\"respCode\":\"TEST_SERVE_SWITCH_FAILED\"}", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SWITCH_ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(SWITCH_ERROR, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                        assertEquals(SWITCH_ERROR, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                        assertEquals("Serve Switch Error", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testServeHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Calculate Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("SERVE_CALCULATE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Calculate Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(2);
                    serveRequest.setServeType("any");
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Balance Deduct Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(3);
                    serveRequest.setServeType("any");
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Sequence Create Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(4);
                    serveRequest.setServeType("any");
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Exception", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("notfound");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Failed: NotFound Plugin Not Found", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("NotFoundPlugin");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Failed: Serve.NotFoundPlugin Plugin Not Found", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(CALCULATE_KEY, SUCCESS, SERVE_KEY, SERVE_KEY));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_01);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_02);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Confirm Failed: Sequence Confirm Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_03);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Confirm Failed: Balance Confirm Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_04);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_04);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_04);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Unexpected Exception: Serve Confirm Exception)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Check Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                httpServerServeCheckFuture(vertx, bunnyOhClient, FAILURE),
                httpServerServeCheckFuture(vertx, bunnyOhClient, UNDEFINED),
                httpServerServeCheckFuture(vertx, bunnyOhClient, SUCCESS),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_05);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_05);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_05);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_05, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertTrue(serveResponse.isSuccess());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_06);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_06);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_06);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_06, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_07);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_07);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_07);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_07, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_08);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_08);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_08);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_08, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals("Serve Confirm Exception", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("TEST_SERVE_SWITCH_FAILED", serveResponse.getRespCode());
                    assertEquals("Test Serve Switch Failed", serveResponse.getRespDesc());
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SWITCH_ERROR);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Switch Error", serveResponse.getRespDesc());
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                    assertEquals("{\"respDesc\":\"Test Serve Switch Failed\",\"respCode\":\"TEST_SERVE_SWITCH_FAILED\"}", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    Map<String, Object> internalRequest = of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SWITCH_ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    internalRequest.put(SERVE_SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_SWITCH_CONFIRM_KEY, SWITCH_ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(SWITCH_ERROR, serveResponse.getInternalResponse().get(SERVE_SWITCH_KEY));
                    assertEquals(SWITCH_ERROR, serveResponse.getInternalResponse().get(SERVE_SWITCH_CONFIRM_KEY));
                    assertEquals("Serve Switch Error", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    private static Future<Void> eventBusServeCheckFuture(VertxTestContext test,
                                                         BunnyEventBus bunnyEventBus,
                                                         String checkValue) {
        return Future.future(f -> {
            val serveRequest = new ServeRequest();
            serveRequest.setChargingType(CHARGING_TYPE_00);
            serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
            serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
            serveRequest.setPaymentValue(1);
            serveRequest.setServeType("test");
            serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, checkValue));
            bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                val serveResponse = async.result();
                assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                assertEquals("test", serveResponse.getServeType());
                assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                assertEquals(checkValue, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                assertNull(serveResponse.getUnexpectedFailure());
                f.complete();
            }));
        });
    }

    private static Future<Void> httpServerServeCheckFuture(Vertx vertx,
                                                           BunnyOhClient bunnyOhClient,
                                                           String checkValue) {
        return Future.future(f -> vertx.executeBlocking(p -> {
            val serveRequest = new ServeRequest();
            serveRequest.setChargingType(CHARGING_TYPE_00);
            serveRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
            serveRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
            serveRequest.setPaymentValue(1);
            serveRequest.setServeType("test");
            serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, checkValue));
            val serveResponse = bunnyOhClient.request(serveRequest);
            assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
            assertEquals("test", serveResponse.getServeType());
            assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
            assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
            assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
            assertEquals(checkValue, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
            assertNull(serveResponse.getUnexpectedFailure());
            p.complete();
        }, false, f));
    }
}
