package com.github.charlemaznable.bunny.rabbittest.mtcp;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;

public final class MtcpRequest extends BunnyBaseRequest<MtcpResponse> {

    public MtcpRequest() {
        this.bunnyAddress = "/mtcp";
    }

    @Override
    public Class<? extends MtcpResponse> responseClass() {
        return MtcpResponse.class;
    }
}
