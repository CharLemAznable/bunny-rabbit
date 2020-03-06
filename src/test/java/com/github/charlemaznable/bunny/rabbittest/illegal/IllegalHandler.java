package com.github.charlemaznable.bunny.rabbittest.illegal;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public final class IllegalHandler
        implements BunnyHandler<IllegalRequest, IllegalResponse> {

    @Override
    public String address() {
        return "/illegal";
    }

    @Override
    public Class<? extends IllegalRequest> getRequestClass() {
        return IllegalRequest.class;
    }

    @Override
    public void execute(IllegalRequest request,
                        Handler<AsyncResult<IllegalResponse>> handler) {
        val response = request.createResponse();
        response.succeed();
        handler.handle(Future.succeededFuture(response));
    }
}
