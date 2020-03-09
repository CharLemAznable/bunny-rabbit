package com.github.charlemaznable.bunny.rabbit.guice;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxConfiguration;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculateHandler;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.charge.ChargeHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptorLoader;
import com.github.charlemaznable.bunny.rabbit.core.query.QueryHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServePlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServePluginLoader;
import com.github.charlemaznable.bunny.rabbit.guice.loader.BunnyHandlerLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.BunnyInterceptorLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.CalculatePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.ServeCallbackPluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.ServePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.guice.Modulee;
import com.github.charlemaznable.core.miner.MinerModular;
import com.github.charlemaznable.core.vertx.guice.VertxModular;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Providers;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.name.Names.named;

public final class BunnyModular {

    private final Module configModule;
    @Getter
    @Accessors(fluent = true)
    private final BunnyEqlerModuleBuilder eqlerModuleBuilder;
    private final List<Class<? extends BunnyHandler>> handlerClasses;
    private final List<Class<? extends BunnyInterceptor>> interceptorClasses;
    private final Module vertxModule;

    private final Module pluginNameMapperModule;
    private final List<Pair<String, Class<? extends CalculatePlugin>>> calculatePlugins;
    private final List<Pair<String, Class<? extends ServePlugin>>> servePlugins;
    private final List<Pair<String, Class<? extends ServeCallbackPlugin>>> serveCallbackPlugins;

    public BunnyModular() {
        this((BunnyConfig) null);
    }

    public BunnyModular(Class<? extends BunnyConfig> configClass) {
        this(new MinerModular().createModule(configClass));
    }

    public BunnyModular(BunnyConfig configImpl) {
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(BunnyConfig.class).toProvider(Providers.of(configImpl));
            }
        });
    }

    public BunnyModular(Module configModule) {
        this.configModule = configModule;
        this.eqlerModuleBuilder = new BunnyEqlerModuleBuilder();
        this.handlerClasses = newArrayList(CalculateHandler.class,
                QueryHandler.class, ChargeHandler.class,
                ServeHandler.class, ServeCallbackHandler.class);
        this.interceptorClasses = newArrayList();
        this.vertxModule = new VertxModular(BunnyVertxConfiguration.class).createModule();

        this.pluginNameMapperModule = new MinerModular().createModule(PluginNameMapper.class);
        this.calculatePlugins = newArrayList();
        this.servePlugins = newArrayList();
        this.serveCallbackPlugins = newArrayList();
    }

    @SafeVarargs
    public final BunnyModular addHandlers(
            Class<? extends BunnyHandler>... handlerClasses) {
        this.handlerClasses.addAll(newArrayList(handlerClasses));
        return this;
    }

    @SafeVarargs
    public final BunnyModular addInterceptors(
            Class<? extends BunnyInterceptor>... interceptorClasses) {
        this.interceptorClasses.addAll(newArrayList(interceptorClasses));
        return this;
    }

    @SafeVarargs
    public final BunnyModular addCalculatePlugins(
            Class<? extends CalculatePlugin>... calculatePlugins) {
        this.calculatePlugins.addAll(newArrayList(calculatePlugins).stream()
                .map(new NamedClassPairFunction<>()).collect(Collectors.toList()));
        return this;
    }

    @SafeVarargs
    public final BunnyModular addServePlugins(
            Class<? extends ServePlugin>... servePlugins) {
        this.servePlugins.addAll(newArrayList(servePlugins).stream()
                .map(new NamedClassPairFunction<>()).collect(Collectors.toList()));
        return this;
    }

    @SafeVarargs
    public final BunnyModular addServeCallbackPlugins(
            Class<? extends ServeCallbackPlugin>... serveCallbackPlugins) {
        this.serveCallbackPlugins.addAll(newArrayList(serveCallbackPlugins).stream()
                .map(new NamedClassPairFunction<>()).collect(Collectors.toList()));
        return this;
    }

    public Module createModule() {
        return Modulee.combine(configModule, eqlerModuleBuilder.build(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        val handlersBinder = newSetBinder(binder(), BunnyHandler.class);
                        for (val handlerClass : handlerClasses) {
                            handlersBinder.addBinding().to(handlerClass).in(SINGLETON);
                        }
                        bind(BunnyHandlerLoader.class).to(BunnyHandlerLoaderImpl.class).in(SINGLETON);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        val interceptorsBinder = newSetBinder(binder(), BunnyInterceptor.class);
                        for (val interceptorClass : interceptorClasses) {
                            interceptorsBinder.addBinding().to(interceptorClass).in(SINGLETON);
                        }
                        bind(BunnyInterceptorLoader.class).to(BunnyInterceptorLoaderImpl.class).in(SINGLETON);
                    }
                },
                vertxModule, pluginNameMapperModule,
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        for (val calculatePlugin : calculatePlugins) {
                            bind(CalculatePlugin.class).annotatedWith(named(
                                    calculatePlugin.getKey())).to(calculatePlugin.getValue());
                        }
                        bind(CalculatePluginLoader.class).to(CalculatePluginLoaderImpl.class).in(SINGLETON);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        for (val servePlugin : servePlugins) {
                            bind(ServePlugin.class).annotatedWith(named(
                                    servePlugin.getKey())).to(servePlugin.getValue());
                        }
                        bind(ServePluginLoader.class).to(ServePluginLoaderImpl.class).in(SINGLETON);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        for (val serveCallbackPlugin : serveCallbackPlugins) {
                            bind(ServeCallbackPlugin.class).annotatedWith(named(
                                    serveCallbackPlugin.getKey())).to(serveCallbackPlugin.getValue());
                        }
                        bind(ServeCallbackPluginLoader.class).to(ServeCallbackPluginLoaderImpl.class).in(SINGLETON);
                    }
                });
    }

    private static class NamedClassPairFunction<T>
            implements Function<Class<? extends T>, Pair<String, Class<? extends T>>> {

        @Override
        public Pair<String, Class<? extends T>> apply(Class<? extends T> clazz) {
            return Pair.of(clazz.getAnnotation(Component.class).value(), clazz);
        }
    }
}
