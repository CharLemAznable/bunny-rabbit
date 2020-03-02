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
                    new BunnyEventBusHandler<>(handler));
        }
    }

    static final class BunnyEventBusHandler
            <T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
            extends BunnyWrapHandler<T, U, Message<String>> {

        public BunnyEventBusHandler(BunnyHandler<T, U> bunnyHandler) {
            super(bunnyHandler);
        }

        @Override
        public String produceRequest(Message<String> message) {
            return message.body();
        }

        @Override
        public void consumeError(Message<String> message, Throwable throwable) {
            message.reply(failureMessage(throwable));
        }

        @Override
        public void consumeResponse(Message<String> message, String response) {
            message.reply(response);
        }
    }
}
