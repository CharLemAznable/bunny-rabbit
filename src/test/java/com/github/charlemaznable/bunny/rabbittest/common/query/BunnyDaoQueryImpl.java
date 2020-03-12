package com.github.charlemaznable.bunny.rabbittest.common.query;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.bunny.rabbittest.common.query.QueryCommon.CHARGING_TYPE_00;
import static com.github.charlemaznable.bunny.rabbittest.common.query.QueryCommon.CHARGING_TYPE_01;
import static com.github.charlemaznable.bunny.rabbittest.common.query.QueryCommon.CHARGING_TYPE_02;
import static com.github.charlemaznable.bunny.rabbittest.common.query.QueryCommon.CHARGING_TYPE_03;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class BunnyDaoQueryImpl implements BunnyDao {

    @Override
    public int updateBalanceByCharge(String chargingType, int chargeValue) {
        return 0; // ignore
    }

    @Override
    public QueryResult queryChargingBalance(String chargingType) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        val queryResult = new QueryResult();
        if (CHARGING_TYPE_00.equals(chargingType)) {
            queryResult.setBalance(0);
            queryResult.setUnit("条");
        } else if (CHARGING_TYPE_01.equals(chargingType)) {
            queryResult.setBalance(100);
            queryResult.setUnit("MB");
        } else if (CHARGING_TYPE_02.equals(chargingType)) {
            return null;
        } else if (CHARGING_TYPE_03.equals(chargingType)) {
            throw new MockException("Query Exception");
        }
        return queryResult;
    }

    @Override
    public void logError(String logId, String seqId, String errorContent) {
        // ignore
    }
}
