package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.AbstractVerticle;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyAbstractVerticle extends AbstractVerticle {

    protected final List<BunnyHandler> handlers;
    protected final BunnyConfig bunnyConfig;
    protected final BunnyLogDao bunnyLogDao;
    protected final NonsenseOptions nonsenseOptions;
    protected final SignatureOptions signatureOptions;

    public BunnyAbstractVerticle(List<BunnyHandler> handlers,
                                 @Nullable BunnyConfig bunnyConfig,
                                 @Nullable BunnyLogDao bunnyLogDao,
                                 @Nullable NonsenseOptions nonsenseOptions,
                                 @Nullable SignatureOptions signatureOptions) {
        this.handlers = newArrayList(handlers);
        this.bunnyConfig = nullThen(bunnyConfig, () -> getConfig(BunnyConfig.class));
        this.bunnyLogDao = nullThen(bunnyLogDao, () -> getEqler(BunnyLogDao.class));
        this.nonsenseOptions = nonsenseOptions;
        this.signatureOptions = signatureOptions;
    }
}
