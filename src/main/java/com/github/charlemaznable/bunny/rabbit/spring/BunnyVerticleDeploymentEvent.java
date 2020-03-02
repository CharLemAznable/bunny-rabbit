package com.github.charlemaznable.bunny.rabbit.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

@Getter
@Setter
public class BunnyVerticleDeploymentEvent extends ApplicationContextEvent {

    private static final long serialVersionUID = -9044518073740741490L;
    private String verticleName;
    private String deploymentId;

    public BunnyVerticleDeploymentEvent(ApplicationContext source) {
        super(source);
    }
}
