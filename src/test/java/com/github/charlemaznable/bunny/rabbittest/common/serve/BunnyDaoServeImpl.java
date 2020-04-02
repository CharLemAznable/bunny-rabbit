package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class BunnyDaoServeImpl implements BunnyDao {

    @Override
    public int updateBalanceByCharge(String chargeCode, int chargeValue) {
        return 0; // ignore
    }

    @Override
    public QueryResult queryChargingBalance(String chargeCode) {
        return null; // ignore
    }

    @Override
    public void logError(String logId, String seqId, String errorContent) {
        assertNotNull(MtcpContext.getTenantId());
        assertNotNull(MtcpContext.getTenantCode());
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());
    }
}
