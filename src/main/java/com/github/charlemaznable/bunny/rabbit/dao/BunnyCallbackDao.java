package com.github.charlemaznable.bunny.rabbit.dao;

import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyCallbackDao {

    /**
     * 查询回调地址
     * 需要服务流水回调状态为初始化
     */
    String queryCallbackUrl(@Param("chargingType") String chargingType,
                            @Param("seqId") String seqId);

    /**
     * 更新回调状态, 自增回调次数
     */
    void updateCallbackState(@Param("chargingType") String chargingType,
                             @Param("seqId") String seqId,
                             @Param("callbackState") String callbackState);

    /**
     * 记录回调日志
     */
    void logCallback(@Param("logId") String logId,
                     @Param("seqId") String seqId,
                     @Param("logType") String logType,
                     @Param("logContent") String logContent);
}
