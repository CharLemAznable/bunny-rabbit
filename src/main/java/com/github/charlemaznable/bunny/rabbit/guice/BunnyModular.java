package com.github.charlemaznable.bunny.rabbit.guice;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculateHandler;
import com.github.charlemaznable.bunny.rabbit.core.charge.ChargeHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.github.charlemaznable.bunny.rabbit.core.common.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.common.ServeCallbackPluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.common.ServePluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.common.SwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.core.query.QueryHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeHandler;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeService;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbit.guice.loader.BunnyHandlerLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.CalculatePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.ServeCallbackPluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.ServePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.guice.loader.SwitchPluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.configservice.ConfigModular;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import com.github.charlemaznable.core.guice.Modulee;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.util.Providers;
import io.vertx.core.Vertx;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.inject.Scopes.SINGLETON;
import static java.util.Objects.nonNull;

@SuppressWarnings("rawtypes")
public final class BunnyModular {

    private final Module configModule;
    private NonsenseOptions nonsenseOptions;
    private SignatureOptions signatureOptions;
    private ChargeCodeMapper chargeCodeMapper;
    private PluginNameMapper pluginNameMapper;
    private Module daoModule;

    public BunnyModular() {
        this((BunnyConfig) null);
    }

    public BunnyModular(Class<? extends BunnyConfig> configClass) {
        this(new ConfigModular().bindClasses(configClass).createModule());
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
        this.configModule = Modulee.combine(configModule, new AbstractModule() {
            @Override
            protected void configure() {
                bind(NonsenseOptions.class).toProvider(Providers.of(nonsenseOptions));
                bind(SignatureOptions.class).toProvider(Providers.of(signatureOptions));
                bind(ChargeCodeMapper.class).toProvider(Providers.of(chargeCodeMapper));
                bind(PluginNameMapper.class).toProvider(Providers.of(pluginNameMapper));
            }
        });
        this.daoModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(BunnyLogDao.class).toProvider(Providers.of(null));
                bind(BunnyDao.class).toProvider(Providers.of(null));
                bind(BunnyCallbackDao.class).toProvider(Providers.of(null));
                bind(BunnyServeDao.class).toProvider(Providers.of(null));
            }
        };
    }

    public BunnyModular nonsenseOptions(NonsenseOptions nonsenseOptions) {
        this.nonsenseOptions = nonsenseOptions;
        return this;
    }

    public BunnyModular signatureOptions(SignatureOptions signatureOptions) {
        this.signatureOptions = signatureOptions;
        return this;
    }

    public BunnyModular chargeCodeMapper(ChargeCodeMapper chargeCodeMapper) {
        this.chargeCodeMapper = chargeCodeMapper;
        return this;
    }

    public BunnyModular pluginNameMapper(PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = pluginNameMapper;
        return this;
    }

    public <T> BunnyModular bindDao(Class<T> clazz, T impl) {
        return bindDao(new AbstractModule() {
            @Override
            protected void configure() {
                bind(clazz).toProvider(Providers.of(impl));
            }
        });
    }

    public <T> BunnyModular bindDao(Class<T> clazz, Class<? extends T> ext) {
        return bindDao(new AbstractModule() {
            @Override
            protected void configure() {
                bind(clazz).to(ext).in(SINGLETON);
            }
        });
    }

    public BunnyModular bindDao(Module module) {
        if (nonNull(module)) {
            this.daoModule = Modulee.override(this.daoModule, module);
        }
        return this;
    }

    public Module createModule() {
        return Modulee.combine(configModule, daoModule, new AbstractModule() {

            @Override
            protected void configure() {
                binder().requireExplicitBindings();
            }

            @Provides
            public BunnyVertxApplication bunnyVertxApplication(Vertx vertx,
                                                               BunnyHandlerLoader handlerLoader,
                                                               @Nullable BunnyConfig bunnyConfig,
                                                               @Nullable BunnyLogDao bunnyLogDao,
                                                               @Nullable NonsenseOptions nonsenseOptions,
                                                               @Nullable SignatureOptions signatureOptions) {
                return new BunnyVertxApplication(vertx, handlerLoader,
                        bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
            }

            @Provides
            public BunnyHandlerLoader bunnyHandlerLoader(Set<BunnyHandler> handlers) {
                return new BunnyHandlerLoaderImpl(handlers);
            }

            @ProvidesIntoSet
            public BunnyHandler calculateHandler(CalculatePluginLoader calculatePluginLoader) {
                return new CalculateHandler(calculatePluginLoader);
            }

            @ProvidesIntoSet
            public BunnyHandler queryHandler(@Nullable ChargeCodeMapper codeMapper,
                                             @Nullable BunnyDao bunnyDao) {
                return new QueryHandler(codeMapper, bunnyDao);
            }

            @ProvidesIntoSet
            public BunnyHandler chargeHandler(@Nullable ChargeCodeMapper codeMapper,
                                              @Nullable BunnyDao bunnyDao) {
                return new ChargeHandler(codeMapper, bunnyDao);
            }

            @ProvidesIntoSet
            public BunnyHandler serveHandler(CalculatePluginLoader calculatePluginLoader,
                                             ServeService serveService,
                                             ServePluginLoader servePluginLoader) {
                return new ServeHandler(calculatePluginLoader, serveService, servePluginLoader);
            }

            @ProvidesIntoSet
            public BunnyHandler serveCallbackHandler(ServeCallbackPluginLoader serveCallbackPluginLoader,
                                                     ServeService serveService,
                                                     @Nullable BunnyCallbackDao bunnyCallbackDao,
                                                     @Nullable BunnyConfig bunnyConfig) {
                return new ServeCallbackHandler(serveCallbackPluginLoader, serveService, bunnyCallbackDao, bunnyConfig);
            }

            @Provides
            public ServeService serveService(SwitchPluginLoader switchPluginLoader,
                                             @Nullable ChargeCodeMapper codeMapper,
                                             @Nullable BunnyServeDao serveDao,
                                             @Nullable BunnyDao bunnyDao) {
                return new ServeService(switchPluginLoader, codeMapper, serveDao, bunnyDao);
            }

            @Provides
            public CalculatePluginLoader calculatePluginLoader(Injector injector,
                                                               @Nullable PluginNameMapper pluginNameMapper) {
                return new CalculatePluginLoaderImpl(injector, pluginNameMapper);
            }

            @Provides
            public ServePluginLoader servePluginLoader(Injector injector,
                                                       @Nullable PluginNameMapper pluginNameMapper) {
                return new ServePluginLoaderImpl(injector, pluginNameMapper);
            }

            @Provides
            public ServeCallbackPluginLoader serveCallbackPluginLoader(Injector injector,
                                                                       @Nullable PluginNameMapper pluginNameMapper) {
                return new ServeCallbackPluginLoaderImpl(injector, pluginNameMapper);
            }

            @Provides
            public SwitchPluginLoader switchPluginLoader(Injector injector,
                                                         @Nullable PluginNameMapper pluginNameMapper) {
                return new SwitchPluginLoaderImpl(injector, pluginNameMapper);
            }
        });
    }
}
