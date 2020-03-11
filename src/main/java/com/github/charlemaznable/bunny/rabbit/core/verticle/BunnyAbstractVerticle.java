package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import io.vertx.core.AbstractVerticle;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyAbstractVerticle extends AbstractVerticle {

    protected final List<BunnyHandler> handlers;
    protected final List<BunnyInterceptor> interceptors;
    protected final BunnyConfig bunnyConfig;
    protected final BunnyLogDao bunnyLogDao;

    public BunnyAbstractVerticle(List<BunnyHandler> handlers,
                                 List<BunnyInterceptor> interceptors,
                                 @Nullable BunnyConfig bunnyConfig,
                                 @Nullable BunnyLogDao bunnyLogDao) {
        this.handlers = newArrayList(handlers);
        this.interceptors = newArrayList(interceptors);
        this.bunnyConfig = nullThen(bunnyConfig,
                () -> getMiner(BunnyConfig.class));
        this.bunnyLogDao = nullThen(bunnyLogDao,
                () -> getEqler(BunnyLogDao.class));
    }
}
