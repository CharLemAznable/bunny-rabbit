package com.github.charlemaznable.bunny.rabbittest.charge;

import com.github.charlemaznable.bunny.client.domain.ChargeRequest;
import com.github.charlemaznable.bunny.client.domain.QueryRequest;
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

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CHARGE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.UNEXPECTED_EXCEPTION;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = ChargeConfiguration.class)
public class ChargeTest {

    static final String CHARGING_TYPE_00 = "00";
    static final String CHARGING_TYPE_01 = "01";
    static final String CHARGING_TYPE_02 = "02";
    static final String CHARGING_TYPE_03 = "03";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testChargeEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ChargeConfiguration.EVENT_BUS_DEPLOYED);

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

    @Test
    public void testChargeHttpServer(Vertx vertx, VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> ChargeConfiguration.HTTP_SERVER_DEPLOYED);

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
