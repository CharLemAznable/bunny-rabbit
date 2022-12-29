package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.util.Objects.isNull;

@SuppressWarnings("rawtypes")
@Component
public final class BunnyHandlerLoaderImpl implements BunnyHandlerLoader {

    private List<BunnyHandler> handlers;

    @Override
    public synchronized List<BunnyHandler> loadHandlers() {
        if (isNull(handlers)) {
            val handlerNames = newArrayList(SpringContext
                    .getBeanNamesForType(BunnyHandler.class));
            Function<String, BunnyHandler> handlerBuilder = SpringContext::getBean;
            handlers = handlerNames.stream().map(handlerBuilder).collect(Collectors.toList());
        }
        return handlers;
    }
}
