package com.github.charlemaznable.bunny.rabbittest.serve;

import com.github.charlemaznable.bunny.client.domain.ServeRequest;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.COMMIT_FAILED;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_00;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_01;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_02;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_03;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_04;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_05;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_06;
import static com.github.charlemaznable.bunny.rabbittest.serve.BunnyServeDaoImpl.CHARGING_TYPE_07;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = ServeConfiguration.class)
public class ServeTest {

    static final String CALCULATE_KEY = "CALC";
    static final String SERVE_KEY = "SERVE";
    static final String SERVE_CHECK_KEY = "SERVE_CHECK";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testServeEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of());
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
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
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
                    serveRequest.setPaymentValue(2);
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
                    serveRequest.setPaymentValue(3);
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
                    serveRequest.setPaymentValue(4);
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
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("notfound");
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
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("NotFound");
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                        assertEquals("Serve Failed: Serve-NotFound Config Not Found", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SERVE_KEY));
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
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(ROLLBACK_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Rollback Failed: Sequence Rollback Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_03);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(ROLLBACK_FAILED)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Serve Rollback Failed: Balance Rollback Failed)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_04);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertNull(serveResponse.getChargingType());
                        assertNull(serveResponse.getServeType());
                        assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                        assertEquals("Test Serve Failed(Unexpected Exception: Serve Rollback Exception)", serveResponse.getRespDesc());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    bunnyEventBus.request(serveRequest, async -> test.verify(() -> {
                        val serveResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                        assertEquals("test", serveResponse.getServeType());
                        assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                        assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                        assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                        assertNull(serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_05);
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
                        assertEquals(COMMIT_FAILED.exception("Sequence Commit Failed").getMessage(), serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_07);
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
                        assertEquals("Serve Commit Exception", serveResponse.getUnexpectedFailure());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testServeHttpServer(Vertx vertx, VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ServeConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of());
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Calculate Error", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("SERVE_CALCULATE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Calculate Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(2);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Balance Deduct Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(3);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("PRE_SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Pre-Serve Failed: Sequence Create Failed", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(4);
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", serveResponse.getRespCode());
                    assertEquals("Unexpected Exception: Serve Exception", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("notfound");
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
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("NotFound");
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("SERVE_FAILED", serveResponse.getRespCode());
                    assertEquals("Serve Failed: Serve-NotFound Config Not Found", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SERVE_KEY));
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
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(ROLLBACK_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Rollback Failed: Sequence Rollback Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_03);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(ROLLBACK_FAILED)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Serve Rollback Failed: Balance Rollback Failed)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_04);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, FAILURE));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertNull(serveResponse.getChargingType());
                    assertNull(serveResponse.getServeType());
                    assertEquals("TEST_SERVE_FAILED(UNEXPECTED_EXCEPTION)", serveResponse.getRespCode());
                    assertEquals("Test Serve Failed(Unexpected Exception: Serve Rollback Exception)", serveResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_00);
                    serveRequest.setPaymentValue(1);
                    serveRequest.setServeType("test");
                    serveRequest.setInternalRequest(of(SERVE_KEY, SUCCESS, SERVE_CHECK_KEY, SUCCESS));
                    val serveResponse = bunnyOhClient.request(serveRequest);
                    assertEquals(CHARGING_TYPE_00, serveResponse.getChargingType());
                    assertEquals("test", serveResponse.getServeType());
                    assertEquals(RESP_CODE_OK, serveResponse.getRespCode());
                    assertEquals(RESP_DESC_SUCCESS, serveResponse.getRespDesc());
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_KEY));
                    assertEquals(SUCCESS, serveResponse.getInternalResponse().get(SERVE_CHECK_KEY));
                    assertNull(serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_05);
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
                    assertEquals(COMMIT_FAILED.exception("Sequence Commit Failed").getMessage(), serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val serveRequest = new ServeRequest();
                    serveRequest.setChargingType(CHARGING_TYPE_07);
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
                    assertEquals("Serve Commit Exception", serveResponse.getUnexpectedFailure());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
