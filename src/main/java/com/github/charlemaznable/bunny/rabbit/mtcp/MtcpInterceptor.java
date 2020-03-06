package com.github.charlemaznable.bunny.rabbit.mtcp;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptor;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;

import javax.annotation.Nullable;

public class MtcpInterceptor implements BunnyInterceptor {

    @Override
    public void preHandle(BunnyBaseRequest<?> request) {
        val extend = request.getExtend();
        MtcpContext.setTenantId(extend.get("tenantId"));
        MtcpContext.setTenantCode(extend.get("tenantCode"));
    }

    @Override
    public void afterCompletion(@Nullable BunnyBaseRequest request,
                                @Nullable BunnyBaseResponse response,
                                @Nullable Throwable throwable) {
        MtcpContext.clear();
    }
}
