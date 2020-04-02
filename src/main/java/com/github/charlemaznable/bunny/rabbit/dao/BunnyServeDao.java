package com.github.charlemaznable.bunny.rabbit.dao;

import org.n3r.eql.EqlTranable;
import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyServeDao extends EqlTranable {

    /**
     * 预扣减, 更新服务余额
     * 先判断余额是否足够, 再进行扣减
     */
    int updateBalanceByPayment(@Param("chargeCode") String chargeCode,
                               @Param("paymentValue") int paymentValue);

    /**
     * 预扣减, 新增流水记录
     */
    int createPreserveSequence(@Param("chargeCode") String chargeCode,
                               @Param("paymentValue") int paymentValue,
                               @Param("callbackUrl") String callbackUrl,
                               @Param("seqId") String seqId);

    /**
     * 确认扣减, 检查流水记录状态
     */
    String queryConfirmedSequence(@Param("chargeCode") String chargeCode,
                                  @Param("seqId") String seqId);

    /**
     * 确认扣减, 更新流水记录状态
     */
    int confirmPreserveSequence(@Param("chargeCode") String chargeCode,
                                @Param("seqId") String seqId,
                                @Param("confirmValue") int confirmValue);

    /**
     * 确认扣减, 更新服务余额
     */
    int updateBalanceByConfirm(@Param("chargeCode") String chargeCode,
                               @Param("seqId") String seqId);
}
