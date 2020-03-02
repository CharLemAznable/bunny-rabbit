package com.github.charlemaznable.bunny.rabbit.handler.dao;

import org.n3r.eql.EqlTranable;
import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyAdvanceDao extends EqlTranable {

    /**
     * 预扣减, 更新服务余额
     * 先判断余额是否足够, 再进行扣减
     */
    int updateBalanceByPayment(@Param("chargingType") String chargingType,
                               @Param("paymentValue") int paymentValue);

    /**
     * 预扣减, 新增流水记录
     */
    int createPaymentSequence(@Param("chargingType") String chargingType,
                              @Param("paymentValue") int paymentValue,
                              @Param("seqId") String seqId);
}
