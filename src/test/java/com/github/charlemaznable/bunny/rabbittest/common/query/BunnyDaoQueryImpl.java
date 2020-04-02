package com.github.charlemaznable.bunny.rabbittest.common.query;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class BunnyDaoQueryImpl implements BunnyDao {

    private static final String CHARGE_CODE_00 = "00.ChargeCode";
    private static final String CHARGE_CODE_01 = "01.ChargeCode";
    private static final String CHARGE_CODE_02 = "02.ChargeCode";
    private static final String CHARGE_CODE_03 = "03.ChargeCode";

    @Override
    public int updateBalanceByCharge(String chargeCode, int chargeValue) {
        return 0; // ignore
    }

    @Override
    public QueryResult queryChargingBalance(String chargeCode) {
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        val queryResult = new QueryResult();
        if (CHARGE_CODE_00.equals(chargeCode)) {
            queryResult.setBalance(0);
            queryResult.setUnit("条");
        } else if (CHARGE_CODE_01.equals(chargeCode)) {
            queryResult.setBalance(100);
            queryResult.setUnit("MB");
        } else if (CHARGE_CODE_02.equals(chargeCode)) {
            return null;
        } else if (CHARGE_CODE_03.equals(chargeCode)) {
            throw new MockException("Query Exception");
        }
        return queryResult;
    }

    @Override
    public void logError(String logId, String seqId, String errorContent) {
        // ignore
    }
}
