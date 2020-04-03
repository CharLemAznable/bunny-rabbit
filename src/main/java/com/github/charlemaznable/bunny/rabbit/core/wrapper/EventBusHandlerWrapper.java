package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.eventbus.Message;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf.failureMessage;

public final class EventBusHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
        extends BunnyHandlerWrapper<T, U, Message<String>> {

    public EventBusHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                                  @Nullable BunnyConfig bunnyConfig,
                                  @Nullable BunnyLogDao bunnyLogDao,
                                  @Nullable NonsenseOptions nonsenseOptions,
                                  @Nullable SignatureOptions signatureOptions) {
        super(bunnyHandler, bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
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
