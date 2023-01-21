package com.github.charlemaznable.bunny.rabbit.spring;

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
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.bunny.rabbit.spring.loader.BunnyHandlerLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.spring.loader.CalculatePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.spring.loader.ServeCallbackPluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.spring.loader.ServePluginLoaderImpl;
import com.github.charlemaznable.bunny.rabbit.spring.loader.SwitchPluginLoaderImpl;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
public class BunnyConfiguration {

    @Bean("com.github.charlemaznable.bunny.rabbit.spring.BunnyVertxStarter")
    public BunnyVertxStarter bunnyVertxStarter(BunnyVertxApplication application) {
        return new BunnyVertxStarter(application);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean("com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication")
    public BunnyVertxApplication bunnyVertxApplication(Vertx vertx,
                                                       BunnyHandlerLoader handlerLoader,
                                                       @Nullable BunnyConfig bunnyConfig,
                                                       @Nullable BunnyLogDao bunnyLogDao,
                                                       @Nullable NonsenseOptions nonsenseOptions,
                                                       @Nullable SignatureOptions signatureOptions) {
        return new BunnyVertxApplication(vertx, handlerLoader,
                bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader")
    public BunnyHandlerLoader bunnyHandlerLoader() {
        return new BunnyHandlerLoaderImpl();
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.calculate.CalculateHandler")
    public CalculateHandler calculateHandler(CalculatePluginLoader calculatePluginLoader) {
        return new CalculateHandler(calculatePluginLoader);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.query.QueryHandler")
    public QueryHandler queryHandler(@Nullable ChargeCodeMapper chargeCodeMapper,
                                     @Nullable BunnyDao bunnyDao) {
        return new QueryHandler(chargeCodeMapper, bunnyDao);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.charge.ChargeHandler")
    public ChargeHandler chargeHandler(@Nullable ChargeCodeMapper chargeCodeMapper,
                                       @Nullable BunnyDao bunnyDao) {
        return new ChargeHandler(chargeCodeMapper, bunnyDao);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.serve.ServeHandler")
    public ServeHandler serveHandler(CalculatePluginLoader calculatePluginLoader,
                                     ServeService serveService,
                                     ServePluginLoader servePluginLoader) {
        return new ServeHandler(calculatePluginLoader, serveService, servePluginLoader);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackHandler")
    public ServeCallbackHandler serveCallbackHandler(ServeCallbackPluginLoader serveCallbackPluginLoader,
                                                     ServeService serveService,
                                                     @Nullable BunnyCallbackDao bunnyCallbackDao,
                                                     @Nullable BunnyConfig bunnyConfig) {
        return new ServeCallbackHandler(serveCallbackPluginLoader, serveService, bunnyCallbackDao, bunnyConfig);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.serve.ServeService")
    public ServeService serveService(SwitchPluginLoader switchPluginLoader,
                                     @Nullable ChargeCodeMapper chargeCodeMapper,
                                     @Nullable BunnyServeDao serveDao,
                                     @Nullable BunnyDao bunnyDao) {
        return new ServeService(switchPluginLoader, chargeCodeMapper, serveDao, bunnyDao);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.common.CalculatePluginLoader")
    public CalculatePluginLoader calculatePluginLoader(@Nullable PluginNameMapper pluginNameMapper) {
        return new CalculatePluginLoaderImpl(pluginNameMapper);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.common.ServePluginLoader")
    public ServePluginLoader servePluginLoader(@Nullable PluginNameMapper pluginNameMapper) {
        return new ServePluginLoaderImpl(pluginNameMapper);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.common.ServeCallbackPluginLoader")
    public ServeCallbackPluginLoader serveCallbackPluginLoader(@Nullable PluginNameMapper pluginNameMapper) {
        return new ServeCallbackPluginLoaderImpl(pluginNameMapper);
    }

    @Bean("com.github.charlemaznable.bunny.rabbit.core.common.SwitchPluginLoader")
    public SwitchPluginLoader switchPluginLoader(@Nullable PluginNameMapper pluginNameMapper) {
        return new SwitchPluginLoaderImpl(pluginNameMapper);
    }
}
