package com.github.charlemaznable.bunny.rabbittest.mtcp;

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
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = MtcpConfiguration.class)
public class MtcpTest {

    private static final String CHARGING_TYPE = "test";
    @Autowired
    private Vertx vertx;
    @Autowired
    private BunnyEventBus bunnyEventBus;
    @Autowired
    private BunnyOhClient bunnyOhClient;

    @Test
    public void testMtcpEventBus(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> MtcpConfiguration.EVENT_BUS_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    val request = new MtcpRequest();
                    request.getExtend().put("tenantId", "tenantId");
                    request.getExtend().put("tenantCode", "tenantCode");
                    bunnyEventBus.request(request, async -> test.verify(() -> {
                        val response = async.result();
                        assertTrue(response.isSuccess());
                        assertEquals("tenantId:tenantCode", response.getContent());
                        f.complete();
                    }));
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }

    @Test
    public void testMtcpHttpServer(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> MtcpConfiguration.HTTP_SERVER_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> vertx.executeBlocking(p -> {
                    val request = new MtcpRequest();
                    request.getExtend().put("tenantId", "tenantId");
                    request.getExtend().put("tenantCode", "tenantCode");
                    val response = bunnyOhClient.request(request);
                    assertTrue(response.isSuccess());
                    assertEquals("tenantId:tenantCode", response.getContent());
                    p.complete();
                }, false, f))
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
