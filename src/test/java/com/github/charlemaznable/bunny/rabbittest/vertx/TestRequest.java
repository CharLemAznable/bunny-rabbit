package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRequest extends BunnyBaseRequest<TestResponse> {

    private String testParameter;

    public TestRequest() {
        this.bunnyAddress = "/test";
    }

    @Override
    public Class<TestResponse> getResponseClass() {
        return TestResponse.class;
    }
}
