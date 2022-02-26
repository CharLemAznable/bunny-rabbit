package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.core.spring.NeoComponentScan;

@NeoComponentScan(basePackageClasses = {
        BunnyVertxApplication.class,
        BunnyComponents.class})
public final class BunnyComponents {
}
