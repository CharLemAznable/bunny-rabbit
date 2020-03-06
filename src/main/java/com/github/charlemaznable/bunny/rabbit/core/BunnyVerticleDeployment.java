package com.github.charlemaznable.bunny.rabbit.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class BunnyVerticleDeployment {

    private final String verticleName;
    private final String deploymentId;
}
