package com.github.charlemaznable.bunny.rabbittest.common.charge;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class BunnyDaoChargeImpl implements BunnyDao {

    private static final String CHARGE_CODE_00 = "00.ChargeCode";
    private static final String CHARGE_CODE_01 = "01.ChargeCode";
    private static final String CHARGE_CODE_02 = "02.ChargeCode";
    private static final String CHARGE_CODE_03 = "03.ChargeCode";

    private int balance00 = 0;
    private int balance01 = 100;

    @Override
    public int updateBalanceByCharge(String chargeCode, int chargeValue) {
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (CHARGE_CODE_00.equals(chargeCode)) {
            balance00 += chargeValue;
        } else if (CHARGE_CODE_01.equals(chargeCode)) {
            balance01 += chargeValue;
        } else if (CHARGE_CODE_02.equals(chargeCode)) {
            return 0;
        } else if (CHARGE_CODE_03.equals(chargeCode)) {
            throw new MockException("Charge Exception");
        }
        return 1;
    }

    @Override
    public QueryResult queryChargingBalance(String chargeCode) {
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        val queryResult = new QueryResult();
        if (CHARGE_CODE_00.equals(chargeCode)) {
            queryResult.setBalance(balance00);
            queryResult.setUnit("条");
        } else if (CHARGE_CODE_01.equals(chargeCode)) {
            queryResult.setBalance(balance01);
            queryResult.setUnit("MB");
        }
        return queryResult;
    }

    @Override
    public void logError(String logId, String seqId, String errorContent) {
        // ignore
    }
}
