package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResponse extends BunnyBaseResponse {

    private String testResult;
}
