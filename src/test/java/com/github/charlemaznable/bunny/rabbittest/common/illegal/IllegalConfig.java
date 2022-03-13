package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;

@DiamondConfig(group = "Bunny", dataId = "illegal")
public interface IllegalConfig extends BunnyConfig {
}
