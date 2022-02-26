package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.core.config.Config;
import com.github.charlemaznable.miner.MinerConfig;
import lombok.val;
import org.n3r.diamond.client.Minerable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;

@MinerConfig(group = "Bunny", dataId = "default")
public interface ChargeCodeMapper extends Minerable {

    /**
     * 服务名称-计量编码 映射关系
     */
    default String chargeCode(String serveName) {
        val configKey = serveName + ".ChargeCode";
        return nullThen(getString(configKey), () -> Config.getStr(configKey, configKey));
    }
}
