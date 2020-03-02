package com.github.charlemaznable.bunny.rabbittest.spring.paymentadvance;

import com.github.charlemaznable.bunny.client.domain.PaymentAdvanceRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentAdvanceResponse;
import com.github.charlemaznable.bunny.client.eventbus.BunnyEventBus;
import com.github.charlemaznable.bunny.client.ohclient.BunnyOhClient;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.DEDUCT_FAILED;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = PaymentAdvanceConfiguration.class)
public class PaymentAdvanceTest {

    private static final String CHARGING_TYPE_00 = "00";
    private static final String CHARGING_TYPE_01 = "01";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testPaymentAdvanceEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentAdvanceConfiguration.EVENT_BUS_DEPLOYED);

        Future.<PaymentAdvanceResponse>future(f -> {
            val advanceRequest = new PaymentAdvanceRequest();
            advanceRequest.setChargingType(CHARGING_TYPE_00);
            advanceRequest.setPaymentValue(1);
            bunnyEventBus.request(advanceRequest, f);
        }).compose(advanceResponse -> {
            assertEquals(CHARGING_TYPE_00, advanceResponse.getChargingType());
            assertTrue(advanceResponse.isSuccess());

            return Future.<PaymentAdvanceResponse>future(f -> {
                val advanceRequest = new PaymentAdvanceRequest();
                advanceRequest.setChargingType(CHARGING_TYPE_00);
                advanceRequest.setPaymentValue(1);
                bunnyEventBus.request(advanceRequest, f);
            });
        }).compose(advanceResponse -> {
            assertEquals(CHARGING_TYPE_00, advanceResponse.getChargingType());
            assertEquals(DEDUCT_FAILED.respCode(), advanceResponse.getRespCode());
            assertEquals(DEDUCT_FAILED.respDesc(), advanceResponse.getRespDesc());

            return Future.succeededFuture();
        }).setHandler(event -> test.completing().handle(event));
    }

    @Test
    public void testPaymentAdvanceHttpServer() {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentAdvanceConfiguration.HTTP_SERVER_DEPLOYED);

        var advanceRequest = new PaymentAdvanceRequest();
        advanceRequest.setChargingType(CHARGING_TYPE_01);
        advanceRequest.setPaymentValue(100);
        var advanceResponse = bunnyOhClient.request(advanceRequest);
        assertEquals(CHARGING_TYPE_01, advanceResponse.getChargingType());
        assertTrue(advanceResponse.isSuccess());

        advanceRequest = new PaymentAdvanceRequest();
        advanceRequest.setChargingType(CHARGING_TYPE_01);
        advanceRequest.setPaymentValue(100);
        advanceResponse = bunnyOhClient.request(advanceRequest);
        assertEquals(CHARGING_TYPE_01, advanceResponse.getChargingType());
        assertEquals(DEDUCT_FAILED.respCode(), advanceResponse.getRespCode());
        assertEquals(DEDUCT_FAILED.respDesc(), advanceResponse.getRespDesc());
    }
}
