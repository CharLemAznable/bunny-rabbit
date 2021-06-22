package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.ServeCallbackRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import com.github.charlemaznable.core.lang.EverythingIsNonNull;
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

import java.util.Map;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CONFIRM_FAILED;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_00;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_01;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_02;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_03;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyCallbackDaoImpl.SEQ_ID_04;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRMED_SEQ_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRMED_SEQ_SUCCESS;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.CONFIRM_FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.BunnyServeDaoImpl.UPDATE_CONFIRM_FAILURE;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("Duplicates")
public class ServeCallbackCommon {

    static final String SERVE_NAME = "serve";
    static final String SERVE_CALLBACK_KEY = "SERVE_CALLBACK";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";
    static final String ERROR = "ERROR";
    private static MockWebServer mockWebServer;
    private static int callback01;
    private static int callback02;
    private static int callback03;
    private static int callback04;

    @EverythingIsNonNull
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
                val requestUrl = checkNotNull(request.getRequestUrl());
                switch (requestUrl.encodedPath()) {
                    case "/callback01":
                        assertEquals(FAILURE, requestUrl
                                .queryParameter(SERVE_CALLBACK_KEY));
                        callback01 += 1;
                        return new MockResponse().setBody("ERROR");
                    case "/callback02":
                        assertEquals(FAILURE, requestUrl
                                .queryParameter(SERVE_CALLBACK_KEY));
                        callback02 += 1;
                        return new MockResponse().setBody("ERROR");
                    case "/callback03":
                        assertEquals(SUCCESS, requestUrl
                                .queryParameter(SERVE_CALLBACK_KEY));
                        callback03 += 1;
                        return new MockResponse().setBody("OK");
                    case "/callback04":
                        assertEquals(SUCCESS, requestUrl
                                .queryParameter(SERVE_CALLBACK_KEY));
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
                    serveCallbackRequest.setServeName("notfound");
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getServeName());
                        assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                        assertEquals("Serve Callback Failed: NotFound Plugin Not Found", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName("NotFoundPlugin");
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getServeName());
                        assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                        assertEquals("Serve Callback Failed: NotFoundPlugin.ServeCallback Plugin Not Found", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getServeName());
                        assertEquals("UNEXPECTED_EXCEPTION", serveCallbackResponse.getRespCode());
                        assertEquals("Unexpected Exception: Serve Callback Error", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, ERROR);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertNull(serveCallbackResponse.getServeName());
                        assertEquals("TEST_SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                        assertEquals("Test Serve Callback Failed", serveCallbackResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals("Serve Confirm Exception", serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_01);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
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
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, SUCCESS);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_03);
                    bunnyEventBus.request(serveCallbackRequest, async -> test.verify(() -> {
                        val serveCallbackResponse = async.result();
                        assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                        assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                        assertNull(serveCallbackResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback03 == 1);
                    f.complete();
                })
        )).onComplete(event -> test.<CompositeFuture>succeedingThenComplete().handle(event));
    }

    public static void testServeCallbackHttpServer(VertxTestContext test, Vertx vertx, BunnyOhClient bunnyOhClient) {
        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName("notfound");
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getServeName());
                    assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                    assertEquals("Serve Callback Failed: NotFound Plugin Not Found", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName("NotFoundPlugin");
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getServeName());
                    assertEquals("SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                    assertEquals("Serve Callback Failed: NotFoundPlugin.ServeCallback Plugin Not Found", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getServeName());
                    assertEquals("UNEXPECTED_EXCEPTION", serveCallbackResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Callback Error", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, ERROR);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertNull(serveCallbackResponse.getServeName());
                    assertEquals("TEST_SERVE_CALLBACK_FAILED", serveCallbackResponse.getRespCode());
                    assertEquals("Test Serve Callback Failed", serveCallbackResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_SUCCESS);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_SUCCESS);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRMED_SEQ_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRMED_SEQ_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals("Serve Confirm Exception", serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, CONFIRM_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, CONFIRM_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(CONFIRM_FAILED.exception("Sequence Confirm Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, UPDATE_CONFIRM_FAILURE);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, UPDATE_CONFIRM_FAILURE);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_00);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertEquals(CONFIRM_FAILED.exception("Balance Confirm Failed").getMessage(), serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveCallbackRequest = new ServeCallbackRequest();
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, FAILURE);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_02);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
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
                    serveCallbackRequest.setServeName(SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_ID, SERVE_NAME);
                    serveCallbackRequest.getContext().put(MtcpContext.TENANT_CODE, SERVE_NAME);
                    Map<String, Object> internalRequest = newHashMap();
                    internalRequest.put(SERVE_CALLBACK_KEY, SUCCESS);
                    serveCallbackRequest.setInternalRequest(internalRequest);
                    serveCallbackRequest.setSeqId(SEQ_ID_04);
                    val serveCallbackResponse = bunnyOhClient.request(serveCallbackRequest);
                    assertEquals(SERVE_NAME, serveCallbackResponse.getServeName());
                    assertEquals(RESP_CODE_OK, serveCallbackResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveCallbackResponse.getRespDesc());
                    assertNull(serveCallbackResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> {
                    await().forever().until(() -> callback04 == 1);
                    f.complete();
                })
        )).onComplete(event -> test.<CompositeFuture>succeedingThenComplete().handle(event));
    }
}
