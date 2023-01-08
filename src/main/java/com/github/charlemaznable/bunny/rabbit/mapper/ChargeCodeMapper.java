package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.configservice.Config;
import com.github.charlemaznable.configservice.ConfigGetter;
import lombok.val;

import static com.github.charlemaznable.core.config.Config.getConfigImpl;
import static com.github.charlemaznable.core.lang.Condition.nullThen;

@Config(keyset = "Bunny", key = "${bunny-config:-default}")
public interface ChargeCodeMapper extends ConfigGetter {

    /**
     * 服务名称-计量编码 映射关系
     */
    default String chargeCode(String serveName) {
        val configKey = serveName + ".ChargeCode";
        return nullThen(getString(configKey), () -> getConfigImpl().getStr(configKey, configKey));
    }
}
