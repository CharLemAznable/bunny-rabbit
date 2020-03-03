package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyEventBusConfig;
import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyHttpServerConfig;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyApplication;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.core.spring.SpringContext;
import io.vertx.core.Vertx;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.core.spring.SpringContext.getBeanNamesForType;

@Component
public final class BunnyStarter {

    private final Vertx vertx;
    private final BunnyEventBusConfig eventBusConfig;
    private final BunnyHttpServerConfig httpServerConfig;
    private volatile boolean deployed;

    @Autowired
    public BunnyStarter(Vertx vertx,
                        @Nullable BunnyEventBusConfig eventBusConfig,
                        @Nullable BunnyHttpServerConfig httpServerConfig) {
        this.vertx = vertx;
        this.eventBusConfig = eventBusConfig;
        this.httpServerConfig = httpServerConfig;
    }

    @EventListener
    public void contextRefreshed(ContextRefreshedEvent event) {
        if (!deployed) {
            synchronized (BunnyStarter.class) {
                if (!deployed) {
                    startBunny(event.getApplicationContext());
                    deployed = true;
                }
            }
        }
    }

    private void startBunny(ApplicationContext applicationContext) {
        new BunnyApplication(eventBusConfig, httpServerConfig,
                getBeanNamesForType(BunnyHandler.class),
                SpringContext::getBean).deploy(vertx, arDeployment -> {
            if (arDeployment.failed()) return;

            val deployment = arDeployment.result();
            val event = new BunnyVerticleDeploymentEvent(applicationContext);
            event.setVerticleName(deployment.getVerticleName());
            event.setDeploymentId(deployment.getDeploymentId());
            applicationContext.publishEvent(event);
        });
    }
}
