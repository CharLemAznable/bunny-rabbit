package com.github.charlemaznable.bunny.rabbittest.serve;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class BunnyDaoServeImpl implements BunnyDao {

    @Override
    public int updateBalanceByCharge(String chargingType, int chargeValue) {
        return 0; // ignore
    }

    @Override
    public QueryResult queryChargingBalance(String chargingType) {
        return null; // ignore
    }

    @Override
    public void logError(String logId, String seqId, String errorContent) {
        assertNotNull(logId);
        assertNotNull(seqId);
        assertNotNull(errorContent);
    }
}
