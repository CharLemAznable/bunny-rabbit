package com.github.charlemaznable.bunny.rabbit.dao;

import lombok.Getter;
import lombok.Setter;
import org.n3r.eql.eqler.annotations.Param;

import java.util.List;

@BunnyEqler
public interface BunnyCallbackDao {

    /**
     * 记录回调请求内容
     */
    int updateCallbackRequest(@Param("chargingType") String chargingType,
                              @Param("seqId") String seqId,
                              @Param("content") String requestContent);

    /**
     * 查询回调地址
     * 需要服务流水回调状态为初始化
     */
    String queryCallbackUrl(@Param("chargingType") String chargingType,
                            @Param("seqId") String seqId);

    /**
     * 记录回调日志
     */
    void logCallback(@Param("logId") String logId,
                     @Param("seqId") String seqId,
                     @Param("logType") String logType,
                     @Param("logContent") String logContent);

    /**
     * 更新回调状态, 自增回调次数
     */
    void updateCallbackState(@Param("chargingType") String chargingType,
                             @Param("seqId") String seqId,
                             @Param("callbackState") String callbackState);

    /**
     * 查询未完成的回调
     */
    List<CallbackRecord> queryCallbackRecords();

    @Getter
    @Setter
    class CallbackRecord {

        private String chargingType;
        private String seqId;
        private String callbackUrl;
        private String requestContent;
    }
}
