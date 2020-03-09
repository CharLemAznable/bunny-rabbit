package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SERVE_CALLBACK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SUCCESS;
import static java.util.Objects.isNull;

@Component("TestServeCallback")
public class TestServeCallbackPlugin implements ServeCallbackPlugin {

    @Override
    public boolean checkRequest(Map<String, Object> request) {
        val callback = request.get(SERVE_CALLBACK_KEY);
        if (isNull(callback)) {
            throw new MockException("Serve Callback Error");
        }
        return SUCCESS.equals(callback);
    }
}
