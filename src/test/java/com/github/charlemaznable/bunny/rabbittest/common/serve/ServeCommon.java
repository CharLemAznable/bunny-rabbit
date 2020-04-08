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
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCalculatePlugin.CALC_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeServiceCommon.SERVE_CHECK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeServiceCommon.SERVE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.TestSwitchPlugin.SWITCH_CONFIRM_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.TestSwitchPlugin.SWITCH_KEY;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServeCommon {

    static final String PRESERVE_NAME = "preserve";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";

    public static void testPreserveEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Calculate Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("SERVE_CALCULATE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Calculate Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Switch Error", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("TEST_SERVE_SWITCH_FAILED", serveResponse.getRespCode());
                        assertEquals("Test Serve Switch Failed", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName("notfound");
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Failed: NotFound Plugin Not Found", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName("NotFoundPlugin");
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getServeName());
                        assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Failed: NotFoundPlugin.Serve Plugin Not Found", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(PRESERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals("Serve Switch Error", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(PRESERVE_NAME, serveResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        val unexpectedFailure = unJson(serveResponse.getUnexpectedFailure());
                        assertEquals("TEST_SERVE_SWITCH_FAILED", unexpectedFailure.get("respCode"));
                        assertEquals("Test Serve Switch Failed", unexpectedFailure.get("respDesc"));
                        f.complete();
                    }));
                })
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testPreserveHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Calculate Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("SERVE_CALCULATE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Calculate Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Switch Error", serveResponse.getRespDesc());
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("TEST_SERVE_SWITCH_FAILED", serveResponse.getRespCode());
                    assertEquals("Test Serve Switch Failed", serveResponse.getRespDesc());
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName("notfound");
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Failed: NotFound Plugin Not Found", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName("NotFoundPlugin");
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getServeName());
                    assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Failed: NotFoundPlugin.Serve Plugin Not Found", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, SUCCESS);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(PRESERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals("Serve Switch Error", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setServeName(PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_ID, PRESERVE_NAME);
                    serveRequest.getContext().put(MtcpContext.TENANT_CODE, PRESERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(CALC_KEY, SUCCESS);
                    internalRequest.put(SWITCH_KEY, SUCCESS);
                    internalRequest.put(SERVE_KEY, SUCCESS);
                    internalRequest.put(SERVE_CHECK_KEY, SUCCESS);
                    internalRequest.put(SWITCH_CONFIRM_KEY, FAILURE);
                    serveRequest.setInternalRequest(internalRequest);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(PRESERVE_NAME, serveResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    val unexpectedFailure = unJson(serveResponse.getUnexpectedFailure());
                    assertEquals("TEST_SERVE_SWITCH_FAILED", unexpectedFailure.get("respCode"));
                    assertEquals("Test Serve Switch Failed", unexpectedFailure.get("respDesc"));
                    p.complete();
                }, false, f))
        )).onComplete(event -> test.<CompositeFuture>completing().handle(event));
    }
}
