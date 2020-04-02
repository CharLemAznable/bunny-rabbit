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
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRMED_SEQ_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRMED_SEQ_SUCCESS;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRM_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CREATE_SEQ_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.UPDATE_BALANCE_ERROR;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.UPDATE_BALANCE_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.UPDATE_CONFIRM_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SUCCESS;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("Duplicates")
public class ServeServiceCommon {

    static final String SERVE_NAME = "serve";
    static final String SERVE_KEY = "SERVE";
    static final String SERVE_CHECK_KEY = "SERVE_CHECK";
    static final String ERROR = "ERROR";
    static final String UNDEFINED = "UNDEFINED";

    public static void testServeServiceEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_BALANCE_ERROR);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_BALANCE_ERROR);
                    serveRequest.setPaymentValue(1);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Exception", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_BALANCE_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_BALANCE_FAILURE);
                    serveRequest.setPaymentValue(1);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Pre-Serve Failed: Balance Deduct Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CREATE_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CREATE_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Pre-Serve Failed: Sequence Create Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Confirm Failed: Sequence Confirm Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Confirm Failed: Balance Confirm Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Unexpected Exception: Serve Confirm Exception)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Check Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_CHECK_FAILED", serveResponse.getRespCode());
                        assertEquals("Test Serve Check Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, UNDEFINED);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(SERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(UNDEFINED, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(SERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(SERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals("Serve Confirm Exception", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(SERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(SERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testServeServiceHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_BALANCE_ERROR);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_BALANCE_ERROR);
                    serveRequest.setPaymentValue(1);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Exception", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_BALANCE_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_BALANCE_FAILURE);
                    serveRequest.setPaymentValue(1);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Balance Deduct Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CREATE_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CREATE_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Sequence Create Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Confirm Failed: Sequence Confirm Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_FAILED(CONFIRM_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Confirm Failed: Balance Confirm Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Unexpected Exception: Serve Confirm Exception)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Check Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, ERROR);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_CHECK_FAILED", serveResponse.getRespCode());
                    assertEquals("Test Serve Check Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, UNDEFINED);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(SERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(UNDEFINED, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(SERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(SERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals("Serve Confirm Exception", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(SERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(SERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    serveRequest.setPaymentValue(1);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(SERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(FAILURE, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
