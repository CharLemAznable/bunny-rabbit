package com.github.charlemaznable.bunny.rabbit.dao;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyDao {

    /**
     * 充值, 更新服务余额
     */
    int updateBalanceByCharge(@Param("chargingType") String chargingType,
                              @Param("chargeValue") int chargeValue);

    /**
     * 以充值类型查询服务余额
     */
    QueryResult queryChargingBalance(@Param("chargingType") String chargingType);

    /**
     * 记录确认/回退预扣减异常日志
     */
    void logError(@Param("logId") String logId,
                  @Param("seqId") String seqId,
                  @Param("errorContent") String errorContent);
}
