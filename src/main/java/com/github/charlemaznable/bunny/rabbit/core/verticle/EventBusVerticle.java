package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.wrapper.EventBusHandlerWrapper;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import lombok.val;
import lombok.var;

import javax.annotation.Nullable;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;

public final class EventBusVerticle extends BunnyAbstractVerticle {

    public static final String EVENT_BUS_VERTICLE = "BUNNY_EVENT_BUS_VERTICLE";

    public EventBusVerticle(@Nullable BunnyConfig bunnyConfig,
                            @Nullable BunnyLogDao bunnyLogDao,
                            List<BunnyHandler> handlers) {
        super(bunnyConfig, bunnyLogDao, handlers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start() {
        var addressPrefix = prependIfMissing(bunnyConfig.addressPrefix(), "/");
        if ("/".equals(addressPrefix)) addressPrefix = "";

        val eventBus = vertx.eventBus();
        for (val handler : handlers) {
            val address = prependIfMissing(handler.address(), "/");
            eventBus.consumer(addressPrefix + address, new EventBusHandlerWrapper(
                    handler, bunnyConfig.interceptors(), bunnyLogDao));
        }
    }
}
