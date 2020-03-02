package com.github.charlemaznable.bunny.rabbittest.spring.paymentrollback;

import com.github.charlemaznable.bunny.client.domain.PaymentRollbackRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentRollbackResponse;
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

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.ROLLBACK_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.ROLLBACK_QUERY_FAILED;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = PaymentRollbackConfiguration.class)
public class PaymentRollbackTest {

    private static final String CHARGING_TYPE_00 = "00";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testPaymentRollbackEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentRollbackConfiguration.EVENT_BUS_DEPLOYED);

        Future.<PaymentRollbackResponse>future(f -> {
            val rollbackRequest = new PaymentRollbackRequest();
            rollbackRequest.setChargingType(CHARGING_TYPE_00);
            rollbackRequest.setPaymentId("100");
            bunnyEventBus.request(rollbackRequest, f);
        }).compose(rollbackResponse -> {
            assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
            assertTrue(rollbackResponse.isSuccess());
            assertEquals(1, rollbackResponse.getRollback());
            assertEquals("条", rollbackResponse.getUnit());

            return Future.<PaymentRollbackResponse>future(f -> {
                val rollbackRequest = new PaymentRollbackRequest();
                rollbackRequest.setChargingType(CHARGING_TYPE_00);
                rollbackRequest.setPaymentId("100");
                bunnyEventBus.request(rollbackRequest, f);
            });
        }).compose(rollbackResponse -> {
            assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
            assertEquals(ROLLBACK_FAILED.respCode(), rollbackResponse.getRespCode());
            assertEquals(ROLLBACK_FAILED.respDesc(), rollbackResponse.getRespDesc());

            return Future.<PaymentRollbackResponse>future(f -> {
                val rollbackRequest = new PaymentRollbackRequest();
                rollbackRequest.setChargingType(CHARGING_TYPE_00);
                rollbackRequest.setPaymentId("101");
                bunnyEventBus.request(rollbackRequest, f);
            });
        }).compose(rollbackResponse -> {
            assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
            assertEquals(ROLLBACK_QUERY_FAILED.respCode(), rollbackResponse.getRespCode());
            assertEquals(ROLLBACK_QUERY_FAILED.respDesc(), rollbackResponse.getRespDesc());

            return Future.succeededFuture();
        }).setHandler(event -> test.completing().handle(event));
    }

    @Test
    public void testPaymentRollbackHttpServer() {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentRollbackConfiguration.HTTP_SERVER_DEPLOYED);

        var rollbackRequest = new PaymentRollbackRequest();
        rollbackRequest.setChargingType(CHARGING_TYPE_00);
        rollbackRequest.setPaymentId("200");
        var rollbackResponse = bunnyOhClient.request(rollbackRequest);
        assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
        assertTrue(rollbackResponse.isSuccess());
        assertEquals(1, rollbackResponse.getRollback());
        assertEquals("条", rollbackResponse.getUnit());

        rollbackRequest = new PaymentRollbackRequest();
        rollbackRequest.setChargingType(CHARGING_TYPE_00);
        rollbackRequest.setPaymentId("200");
        rollbackResponse = bunnyOhClient.request(rollbackRequest);
        assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
        assertEquals(ROLLBACK_FAILED.respCode(), rollbackResponse.getRespCode());
        assertEquals(ROLLBACK_FAILED.respDesc(), rollbackResponse.getRespDesc());

        rollbackRequest = new PaymentRollbackRequest();
        rollbackRequest.setChargingType(CHARGING_TYPE_00);
        rollbackRequest.setPaymentId("201");
        rollbackResponse = bunnyOhClient.request(rollbackRequest);
        assertEquals(CHARGING_TYPE_00, rollbackResponse.getChargingType());
        assertEquals(ROLLBACK_QUERY_FAILED.respCode(), rollbackResponse.getRespCode());
        assertEquals(ROLLBACK_QUERY_FAILED.respDesc(), rollbackResponse.getRespDesc());
    }
}
