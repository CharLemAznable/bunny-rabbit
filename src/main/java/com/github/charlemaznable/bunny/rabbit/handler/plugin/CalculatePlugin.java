package com.github.charlemaznable.bunny.rabbit.handler.plugin;

import javax.annotation.Nonnull;
import java.util.Map;

public interface CalculatePlugin {

    @Nonnull
    CalculateResult calculate(Map<String, String> chargingParameters);
}
