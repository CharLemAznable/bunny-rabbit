package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptorLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.util.Objects.isNull;

@Component
public final class BunnyInterceptorLoaderImpl implements BunnyInterceptorLoader {

    private List<BunnyInterceptor> interceptors;

    @Override
    public List<BunnyInterceptor> loadInterceptors() {
        if (isNull(interceptors)) {
            val handlerNames = newArrayList(SpringContext
                    .getBeanNamesForType(BunnyInterceptor.class));
            Function<String, BunnyInterceptor> handlerBuilder = SpringContext::getBean;
            interceptors = handlerNames.stream().map(handlerBuilder).collect(Collectors.toList());
        }
        return interceptors;
    }
}
