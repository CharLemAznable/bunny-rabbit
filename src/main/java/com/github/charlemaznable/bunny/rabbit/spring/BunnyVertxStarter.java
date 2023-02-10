package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

public final class BunnyVertxStarter {

    private final BunnyVertxApplication application;
    private volatile boolean deployed;

    public BunnyVertxStarter(BunnyVertxApplication application) {
        this.application = checkNotNull(application);
    }

    @EventListener
    public void contextRefreshed(ContextRefreshedEvent event) {
        deploy(event.getApplicationContext());
    }

    public synchronized void deploy(ApplicationContext applicationContext) {
        if (deployed) return;
        deployed = true;
        application.deploy(asyncResult -> {
            if (asyncResult.failed()) return;

            val deployment = asyncResult.result();
            val event = new BunnyDeployedEvent(applicationContext);
            event.setVerticleName(deployment.getVerticleName());
            event.setDeploymentId(deployment.getDeploymentId());
            applicationContext.publishEvent(event);
        });
    }
}
