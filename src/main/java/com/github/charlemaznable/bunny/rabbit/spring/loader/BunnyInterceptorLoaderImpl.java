package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptorLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Component
public final class BunnyInterceptorLoaderImpl implements BunnyInterceptorLoader {

    @Override
    public List<BunnyInterceptor> loadInterceptors() {
        val handlerNames = newArrayList(SpringContext
                .getBeanNamesForType(BunnyInterceptor.class));
        Function<String, BunnyInterceptor> handlerBuilder = SpringContext::getBean;
        return handlerNames.stream().map(handlerBuilder).collect(Collectors.toList());
    }
}
