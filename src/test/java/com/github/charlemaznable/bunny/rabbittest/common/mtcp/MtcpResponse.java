package com.github.charlemaznable.bunny.rabbittest.common.mtcp;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MtcpResponse extends BunnyBaseResponse {

    private String content;
}
