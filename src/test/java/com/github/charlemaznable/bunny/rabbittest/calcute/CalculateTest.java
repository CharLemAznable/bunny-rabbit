package com.github.charlemaznable.bunny.rabbittest.calcute;

import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
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

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.of;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = CalculateConfiguration.class)
public class CalculateTest {

    private static final String CHARGING_TYPE = "test";
    static final String CALCULATE_KEY = "CALC";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testCalcuteEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CalculateConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertEquals(CHARGING_TYPE, calculateResponse.getChargingType());
                        assertEquals(1, calculateResponse.getCalculate());
                        assertEquals("条", calculateResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
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
                    calculateRequest.setChargingParameters(of());
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
                    calculateRequest.setChargingParameters(of());
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
                    calculateRequest.setChargingType("NotFound");
                    calculateRequest.setChargingParameters(of());
                    bunnyEventBus.request(calculateRequest, async -> test.verify(() -> {
                        val calculateResponse = async.result();
                        assertNull(calculateResponse.getChargingType());
                        assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                        assertEquals("Charge Calculate Failed: Calculate-NotFound Config Not Found", calculateResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testCalcuteHttpServer(Vertx vertx, VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CalculateConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
                    calculateRequest.setChargingParameters(of(CALCULATE_KEY, SUCCESS));
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertEquals(CHARGING_TYPE, calculateResponse.getChargingType());
                    assertEquals(1, calculateResponse.getCalculate());
                    assertEquals("条", calculateResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType(CHARGING_TYPE);
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
                    calculateRequest.setChargingParameters(of());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("UNEXPECTED_EXCEPTION", calculateResponse.getRespCode());
                    assertEquals("Unexpected Exception: Calculate Error", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("notfound");
                    calculateRequest.setChargingParameters(of());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: NotFound Plugin Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val calculateRequest = new CalculateRequest();
                    calculateRequest.setChargingType("NotFound");
                    calculateRequest.setChargingParameters(of());
                    val calculateResponse = bunnyOhClient.request(calculateRequest);
                    assertNull(calculateResponse.getChargingType());
                    assertEquals("CALCULATE_FAILED", calculateResponse.getRespCode());
                    assertEquals("Charge Calculate Failed: Calculate-NotFound Config Not Found", calculateResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
