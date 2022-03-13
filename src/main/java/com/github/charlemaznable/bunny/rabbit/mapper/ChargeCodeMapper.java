package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.configservice.ConfigGetter;
import com.github.charlemaznable.configservice.apollo.ApolloConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.github.charlemaznable.core.config.Config;
import lombok.val;

import static com.github.charlemaznable.core.lang.Condition.nullThen;

@ApolloConfig(namespace = "Bunny", propertyName = "${bunny-config:-default}")
@DiamondConfig(group = "Bunny", dataId = "${bunny-config:-default}")
public interface ChargeCodeMapper extends ConfigGetter {

    /**
     * 服务名称-计量编码 映射关系
     */
    default String chargeCode(String serveName) {
        val configKey = serveName + ".ChargeCode";
        return nullThen(getString(configKey), () -> Config.getStr(configKey, configKey));
    }
}
