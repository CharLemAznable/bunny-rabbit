package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;

public final class IllegalRequest extends BunnyBaseRequest<IllegalResponse> {

    public IllegalRequest() {
        this.bunnyAddress = "/illegal";
    }

    @Override
    public Class<? extends IllegalResponse> responseClass() {
        return IllegalResponse.class;
    }
}
