package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.core.miner.MinerConfig;

@MinerConfig(group = "Bunny", dataId = "illegal")
public interface IllegalConfig extends BunnyConfig {
}
