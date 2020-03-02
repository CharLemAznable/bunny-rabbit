package com.github.charlemaznable.bunny.rabbit.handler.dao;

import org.n3r.eql.EqlTranable;
import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyRollbackDao extends EqlTranable {

    /**
     * 扣费回退, 更新服务余额
     */
    int updateBalanceByRollback(@Param("chargingType") String chargingType,
                                @Param("rollbackValue") int rollbackValue);

    /**
     * 扣费回退, 更新流水记录状态
     */
    int rollbackPaymentSequence(@Param("chargingType") String chargingType,
                                @Param("seqId") String seqId);
}
