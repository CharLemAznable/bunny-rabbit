package com.github.charlemaznable.bunny.rabbittest.spring.query;

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

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.QUERY_FAILED;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = QueryConfiguration.class)
public class QueryTest {

    private static final String CHARGING_TYPE_00 = "00";
    private static final String CHARGING_TYPE_01 = "01";
    private static final String CHARGING_TYPE_02 = "02";
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testQueryEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> QueryConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_00);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(CHARGING_TYPE_00, queryResponse.getChargingType());
                        assertEquals(0, queryResponse.getBalance());
                        assertEquals("条", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_01);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(CHARGING_TYPE_01, queryResponse.getChargingType());
                        assertEquals(100, queryResponse.getBalance());
                        assertEquals("MB", queryResponse.getUnit());
                        f.complete();
                    }));
                }),
                Future.<Void>future(f -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_02);
                    bunnyEventBus.request(queryRequest, async -> test.verify(() -> {
                        val queryResponse = async.result();
                        assertEquals(CHARGING_TYPE_02, queryResponse.getChargingType());
                        assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                        assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testQueryHttpServer(Vertx vertx, VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> QueryConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_00);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_00, queryResponse.getChargingType());
                    assertEquals(0, queryResponse.getBalance());
                    assertEquals("条", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_01);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_01, queryResponse.getChargingType());
                    assertEquals(100, queryResponse.getBalance());
                    assertEquals("MB", queryResponse.getUnit());
                    p.complete();
                }, false, f)),
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val queryRequest = new QueryRequest();
                    queryRequest.setChargingType(CHARGING_TYPE_02);
                    val queryResponse = bunnyOhClient.request(queryRequest);
                    assertEquals(CHARGING_TYPE_02, queryResponse.getChargingType());
                    assertEquals(QUERY_FAILED.respCode(), queryResponse.getRespCode());
                    assertEquals(QUERY_FAILED.respDesc(), queryResponse.getRespDesc());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
