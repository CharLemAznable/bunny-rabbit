package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.core.BunnyVertxApplication;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.miner.MinerScan;
import com.github.charlemaznable.core.spring.ComplexComponentScan;

@ComplexComponentScan(basePackageClasses = {
        BunnyVertxApplication.class,
        BunnyComponents.class})
@MinerScan(basePackageClasses = {
        PluginNameMapper.class
})
public final class BunnyComponents {
}
