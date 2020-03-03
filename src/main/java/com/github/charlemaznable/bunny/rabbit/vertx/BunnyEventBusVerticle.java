package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyEventBusConfig;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyWrapHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import lombok.val;
import lombok.var;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyElf.failureMessage;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static org.apache.commons.lang3.StringUtils.prependIfMissing;

public final class BunnyEventBusVerticle extends AbstractVerticle {

    public static final String EVENT_BUS_VERTICLE = "BUNNY_EVENT_BUS_VERTICLE";
    private final Iterable<BunnyHandler<?, ?>> handlers;
    private final BunnyEventBusConfig config;

    public BunnyEventBusVerticle(Iterable<BunnyHandler<?, ?>> handlers) {
        this.handlers = newArrayList(handlers);
        this.config = getMiner(BunnyEventBusConfig.class);
    }

    @Override
    public void start() {
        var addressPrefix = prependIfMissing(config.addressPrefix(), "/");
        if ("/".equals(addressPrefix)) addressPrefix = "";

        val eventBus = vertx.eventBus();
        for (val handler : handlers) {
            val address = prependIfMissing(handler.address(), "/");
            eventBus.consumer(addressPrefix + address,
                    new BunnyEventBusHandler<>(handler, config.interceptors()));
        }
    }

    static final class BunnyEventBusHandler
            <T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
            extends BunnyWrapHandler<T, U, Message<String>> {

        private final List<BunnyEventBusInterceptor> interceptors;

        public BunnyEventBusHandler(BunnyHandler<T, U> bunnyHandler,
                                    List<BunnyEventBusInterceptor> interceptors) {
            super(bunnyHandler);
            this.interceptors = newArrayList(interceptors);
        }

        @Override
        public String produceRequest(Message<String> message) {
            interceptors.forEach(interceptor ->
                    interceptor.preHandle(message));
            return message.body();
        }

        @Override
        public void consumeError(Message<String> message, Throwable throwable) {
            message.reply(failureMessage(throwable));
            iterateReverse(message, null, throwable);
        }

        @Override
        public void consumeResponse(Message<String> message, String response) {
            message.reply(response);
            iterateReverse(message, response, null);
        }

        private void iterateReverse(Message<String> message,
                                    @Nullable String response,
                                    @Nullable Throwable throwable) {
            if (interceptors.isEmpty()) return;

            val iterator = interceptors.listIterator(interceptors.size());
            while (iterator.hasPrevious()) {
                val interceptor = iterator.previous();
                interceptor.afterCompletion(message, response, throwable);
            }
        }
    }
}
