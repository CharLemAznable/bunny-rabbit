package com.github.charlemaznable.bunny.rabbittest.callback;

import com.github.charlemaznable.core.net.common.HttpStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.time.Duration.ofMillis;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith({SpringExtension.class, VertxExtension.class})
@ContextConfiguration(classes = CallbackVerticleConfiguration.class)
public class CallbackVerticleTest {

    private static MockWebServer mockWebServer;
    private static boolean callback01;
    private static boolean callback02;

    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                val requestUrl = request.getRequestUrl();
                switch (requestUrl.encodedPath()) {
                    case "/callback01":
                        callback01 = true;
                        return new MockResponse().setBody("OK");
                    case "/callback02":
                        callback02 = true;
                        return new MockResponse().setBody("OK");
                    default:
                        return new MockResponse()
                                .setResponseCode(HttpStatus.NOT_FOUND.value())
                                .setBody(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
            }
        });
        mockWebServer.start(9040);
    }

    @SneakyThrows
    @AfterAll
    public static void afterAll() {
        mockWebServer.shutdown();
    }

    @Test
    public void testCallbackVerticle(VertxTestContext test) {
        await().timeout(ofMillis(20000)).pollInterval(ofMillis(500))
                .until(() -> CallbackVerticleConfiguration.CALLBACK_DEPLOYED);

        CompositeFuture.all(newArrayList(
                Future.<Void>future(f -> {
                    await().forever().untilAsserted(() -> assertTrue(callback01));
                    f.complete();
                }),
                Future.<Void>future(f -> {
                    await().forever().untilAsserted(() -> assertTrue(callback02));
                    f.complete();
                })
        )).setHandler(event -> test.<CompositeFuture>completing().handle(event));
    }
}
