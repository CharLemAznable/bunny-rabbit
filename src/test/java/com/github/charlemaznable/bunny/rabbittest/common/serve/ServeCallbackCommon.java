package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.ServeCallbackRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.core.net.common.HttpStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.n3r.eql.mtcp.MtcpContext;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.COMMIT_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.ROLLBACK_FAILED;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_00;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_01;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_02;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_03;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_04;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_00;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_01;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_02;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_03;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_04;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_05;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_06;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CHARGING_TYPE_07;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeCallbackCommon {

    static final String SERVE_CALLBACK_KEY = "SERVE_CALLBACK";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";
    private static MockWebServer mockWebServer;
    private static int callback01;
    private static int callback02;
    private static int callback03;
    private static int callback04;

    @SneakyThrows
    public static void beforeAll() {
        callback01 = 0;
        callback02 = 0;
        callback03 = 0;
        callback04 = 0;
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                val requestUrl = request.getRequestUrl();
                switch (requestUrl.encodedPath()) {
                    case "/callback01":
                        callback01 += 1;
                        return new MockResponse().setBody("ERROR");
                    case "/callback02":
                        callback02 += 1;
                        return new MockResponse().setBody("ERROR");
                    case "/callback03":
                        callback03 += 1;
                        return new MockResponse().setBody("OK");
                    case "/callback04":
                        callback04 += 1;
                        return new MockResponse().setBody("OK");
                    default:
                        return new MockResponse()
                                .setResponseCode(HttpStatus.NOT_FOUND.value())
                                .setBody(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
            }
        });
        mockWebServer.start(9030);
    }

    @SneakyThrows
    public static void afterAll() {
        mockWebServer.shutdown();
    }

    public static void testServeCallbackEventBus(VertxTestContext test, BunnyEventBus bunnyEventBus) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("notfound");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getChargingType());
                        assertNull(serveCallbackResponse.getServeType());
                        assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                        assertEquals("Serve Callback Failed: NotFound Plugin Not Found", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("NotFoundPlugin");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getChargingType());
                        assertNull(serveCallbackResponse.getServeType());
                        assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                        assertEquals("Serve Callback Failed: NotFoundPlugin Plugin Not Found", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getChargingType());
                        assertNull(serveCallbackResponse.getServeType());
                        assertEquals("UNEXPECTED_EXCEPTION", serveCallbackResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Callback Error", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_01);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback01 == 3);
                    f.complete();
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_01);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_01, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertTrue(serveCallbackResponse.isSuccess());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_02);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_02, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(ROLLBACK_FAILED.exception("Sequence Rollback Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_03);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_03, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(ROLLBACK_FAILED.exception("Balance Rollback Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_04);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_04);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_04);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_04, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals("Serve Rollback Exception", serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_03);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback03 == 1);
                    f.complete();
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_05);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_05);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_05);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_05, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals("OK", serveCallbackResponse.getRespCode());
                        assertEquals("SUCCESS", serveCallbackResponse.getRespDesc());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_06);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_06);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_06);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_06, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(COMMIT_FAILED.exception("Sequence Commit Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_07);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_07);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_07);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(CHARGING_TYPE_07, serveCallbackResponse.getChargingType());
                        assertEquals("test", serveCallbackResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals("Serve Commit Exception", serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    public static void testServeCallbackHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("notfound");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getChargingType());
                    assertNull(serveCallbackResponse.getServeType());
                    assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                    assertEquals("Serve Callback Failed: NotFound Plugin Not Found", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("NotFoundPlugin");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getChargingType());
                    assertNull(serveCallbackResponse.getServeType());
                    assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                    assertEquals("Serve Callback Failed: NotFoundPlugin Plugin Not Found", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, null));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getChargingType());
                    assertNull(serveCallbackResponse.getServeType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveCallbackResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Callback Error", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_02);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_00, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback02 == 3);
                    f.complete();
                }),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_01);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_01);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_01);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_01, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertTrue(serveCallbackResponse.isSuccess());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_02);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_02);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_02);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_02, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(ROLLBACK_FAILED.exception("Sequence Rollback Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_03);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_03);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_03);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_03, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(ROLLBACK_FAILED.exception("Balance Rollback Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_04);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_04);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_04);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, FAILURE));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_04, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals("Serve Rollback Exception", serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_00);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_00);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_04);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_00, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback04 == 1);
                    f.complete();
                }),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_05);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_05);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_05);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_05, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals("OK", serveCallbackResponse.getRespCode());
                    assertEquals("SUCCESS", serveCallbackResponse.getRespDesc());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_06);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_06);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_06);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_06, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(COMMIT_FAILED.exception("Sequence Commit Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setChargingType(CHARGING_TYPE_07);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CHARGING_TYPE_07);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CHARGING_TYPE_07);
                    serveCallbackRequest.setServeType("test");
                    serveCallbackRequest.setInternalRequest(of(SERVE_CALLBACK_KEY, SUCCESS));
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(CHARGING_TYPE_07, serveCallbackResponse.getChargingType());
                    assertEquals("test", serveCallbackResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals("Serve Commit Exception", serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
