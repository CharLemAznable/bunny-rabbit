package com.github.charlemaznable.bunny.rabbit.handler.plugin;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
public class CalculateResult {

    private boolean success;
    private String failCode;
    private String failDesc;
    private int calculate;
    private String unit;

    public static CalculateResult successResult(int calculate, String unit) {
        val result = new CalculateResult();
        result.setSuccess(true);
        result.setCalculate(calculate);
        result.setUnit(unit);
        return result;
    }

    public static CalculateResult failureResult(String failCode, String failDesc) {
        val result = new CalculateResult();
        result.setSuccess(false);
        result.setFailCode(failCode);
        result.setFailDesc(failDesc);
        return result;
    }
}
