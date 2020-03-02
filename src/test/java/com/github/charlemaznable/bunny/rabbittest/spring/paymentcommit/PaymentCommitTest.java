package com.github.charlemaznable.bunny.rabbittest.spring.paymentcommit;

import com.github.charlemaznable.bunny.client.domain.PaymentCommitRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentCommitResponse;
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

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_QUERY_FAILED;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = PaymentCommitConfiguration.class)
public class PaymentCommitTest {

    private static final String CHARGING_TYPE_00 = "00";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testPaymentCommitEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentCommitConfiguration.EVENT_BUS_DEPLOYED);

        Future.<PaymentCommitResponse>future(f -> {
            val commitRequest = new PaymentCommitRequest();
            commitRequest.setChargingType(CHARGING_TYPE_00);
            commitRequest.setPaymentId("100");
            bunnyEventBus.request(commitRequest, f);
        }).compose(commitResponse -> {
            assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
            assertTrue(commitResponse.isSuccess());
            assertEquals(1, commitResponse.getCommit());
            assertEquals("条", commitResponse.getUnit());

            return Future.<PaymentCommitResponse>future(f -> {
                val commitRequest = new PaymentCommitRequest();
                commitRequest.setChargingType(CHARGING_TYPE_00);
                commitRequest.setPaymentId("100");
                bunnyEventBus.request(commitRequest, f);
            });
        }).compose(commitResponse -> {
            assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
            assertEquals(COMMIT_FAILED.respCode(), commitResponse.getRespCode());
            assertEquals(COMMIT_FAILED.respDesc(), commitResponse.getRespDesc());

            return Future.<PaymentCommitResponse>future(f -> {
                val commitRequest = new PaymentCommitRequest();
                commitRequest.setChargingType(CHARGING_TYPE_00);
                commitRequest.setPaymentId("101");
                bunnyEventBus.request(commitRequest, f);
            });
        }).compose(commitResponse -> {
            assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
            assertEquals(COMMIT_QUERY_FAILED.respCode(), commitResponse.getRespCode());
            assertEquals(COMMIT_QUERY_FAILED.respDesc(), commitResponse.getRespDesc());

            return Future.succeededFuture();
        }).setHandler(event -> test.completing().handle(event));
    }

    @Test
    public void testPaymentCommitHttpServer() {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> PaymentCommitConfiguration.HTTP_SERVER_DEPLOYED);

        var commitRequest = new PaymentCommitRequest();
        commitRequest.setChargingType(CHARGING_TYPE_00);
        commitRequest.setPaymentId("200");
        var commitResponse = bunnyOhClient.request(commitRequest);
        assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
        assertTrue(commitResponse.isSuccess());
        assertEquals(1, commitResponse.getCommit());
        assertEquals("条", commitResponse.getUnit());

        commitRequest = new PaymentCommitRequest();
        commitRequest.setChargingType(CHARGING_TYPE_00);
        commitRequest.setPaymentId("200");
        commitResponse = bunnyOhClient.request(commitRequest);
        assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
        assertEquals(COMMIT_FAILED.respCode(), commitResponse.getRespCode());
        assertEquals(COMMIT_FAILED.respDesc(), commitResponse.getRespDesc());

        commitRequest = new PaymentCommitRequest();
        commitRequest.setChargingType(CHARGING_TYPE_00);
        commitRequest.setPaymentId("201");
        commitResponse = bunnyOhClient.request(commitRequest);
        assertEquals(CHARGING_TYPE_00, commitResponse.getChargingType());
        assertEquals(COMMIT_QUERY_FAILED.respCode(), commitResponse.getRespCode());
        assertEquals(COMMIT_QUERY_FAILED.respDesc(), commitResponse.getRespDesc());
    }
}
