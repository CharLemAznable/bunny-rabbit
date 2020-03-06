package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Component
public final class BunnyHandlerLoaderImpl implements BunnyHandlerLoader {

    @Override
    public List<BunnyHandler> loadHandlers() {
        val handlerNames = newArrayList(SpringContext
                .getBeanNamesForType(BunnyHandler.class));
        Function<String, BunnyHandler> handlerBuilder = SpringContext::getBean;
        return handlerNames.stream().map(handlerBuilder).collect(Collectors.toList());
    }
}
