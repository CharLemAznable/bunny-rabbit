package com.github.charlemaznable.bunny.rabbit.core.serve;

import java.util.Map;

public interface ServeCallbackPlugin {

    boolean checkRequest(Map<String, Object> request);
}
