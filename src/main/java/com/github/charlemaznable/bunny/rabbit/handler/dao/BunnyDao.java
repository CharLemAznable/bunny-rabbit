package com.github.charlemaznable.bunny.rabbit.handler.dao;

import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyDao {

    /**
     * 以充值类型查询服务余额
     */
    QueryResult queryChargingBalance(@Param("chargingType") String chargingType);

    /**
     * 充值, 更新服务余额
     */
    int updateBalanceByCharge(@Param("chargingType") String chargingType,
                              @Param("chargeValue") int chargeValue);

    /**
     * 查询流水记录
     */
    SeqResult queryPaymentSequence(@Param("chargingType") String chargingType,
                                   @Param("seqId") String seqId);

    /**
     * 扣费确认, 更新流水记录状态
     */
    int commitPaymentSequence(@Param("chargingType") String chargingType,
                              @Param("seqId") String seqId);
}
