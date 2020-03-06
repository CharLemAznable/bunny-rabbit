package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import io.vertx.core.AbstractVerticle;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyAbstractVerticle extends AbstractVerticle {

    protected final BunnyConfig bunnyConfig;
    protected final BunnyLogDao bunnyLogDao;
    protected final List<BunnyHandler> handlers;

    public BunnyAbstractVerticle(@Nullable BunnyConfig bunnyConfig,
                                 @Nullable BunnyLogDao bunnyLogDao,
                                 List<BunnyHandler> handlers) {
        this.bunnyConfig = nullThen(bunnyConfig,
                () -> getMiner(BunnyConfig.class));
        this.bunnyLogDao = nullThen(bunnyLogDao,
                () -> getEqler(BunnyLogDao.class));
        this.handlers = newArrayList(handlers);
    }
}
