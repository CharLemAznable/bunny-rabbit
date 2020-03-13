package com.github.charlemaznable.bunny.rabbit.core.serve;

import java.util.Map;

class ServeContext {

    /**
     * 计费类型
     */
    String chargingType;
    /**
     * 扩展参数
     */
    Map<String, Object> context;
    /**
     * 费用计量
     */
    Integer paymentValue;
    /**
     * 计费流水号
     */
    String seqId;
    /**
     * 服务类型
     */
    String serveType;
    /**
     * 服务请求
     */
    Map<String, Object> internalRequest;
    /**
     * 服务结果回调地址
     */
    String callbackUrl;
    /**
     * 服务调用结果
     */
    boolean returnSuccess;
    /**
     * 服务异常
     */
    Throwable internalThrowable;
    /**
     * 服务响应
     */
    Map<String, Object> internalResponse;
    /**
     * 服务下发确认计量
     */
    Integer confirmValue;
    /**
     * 非期望的内部异常
     */
    Throwable unexpectedThrowable;
}
