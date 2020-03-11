package com.github.charlemaznable.bunny.rabbittest.common.mtcp;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

@Component
public final class MtcpHandler
        implements BunnyHandler<MtcpRequest, MtcpResponse> {

    @Override
    public String address() {
        return "/mtcp";
    }

    @Override
    public Class<? extends MtcpRequest> getRequestClass() {
        return MtcpRequest.class;
    }

    @Override
    public void execute(MtcpRequest request,
                        Handler<AsyncResult<MtcpResponse>> handler) {
        val response = request.createResponse();
        response.succeed();
        response.setContent(MtcpContext.getTenantId() + ":" + MtcpContext.getTenantCode());
        handler.handle(Future.succeededFuture(response));
    }
}
