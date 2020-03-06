package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

@Component
public final class BunnyVertxStarter {

    private final BunnyVertxApplication application;
    private volatile boolean deployed;

    @Autowired
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
