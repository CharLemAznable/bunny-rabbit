package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.client.spring.BunnyEventBusImport;
import com.github.charlemaznable.bunny.client.spring.BunnyOhClientImport;
import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.core.miner.MinerScan;
import com.github.charlemaznable.core.spring.ComplexComponentScan;
import com.github.charlemaznable.core.vertx.spring.SpringVertxImport;

@ComplexComponentScan(basePackageClasses = {
        BunnyVertxApplication.class,
        BunnyComponents.class})
@MinerScan
@SpringVertxImport
@BunnyOhClientImport
@BunnyEventBusImport
public final class BunnyComponents {
}
