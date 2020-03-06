package com.github.charlemaznable.bunny.rabbittest.charge;

import com.github.charlemaznable.bunny.rabbit.core.query.QueryResult;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbittest.common.MockException;
import lombok.val;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.bunny.rabbittest.charge.ChargeTest.CHARGING_TYPE_00;
import static com.github.charlemaznable.bunny.rabbittest.charge.ChargeTest.CHARGING_TYPE_01;
import static com.github.charlemaznable.bunny.rabbittest.charge.ChargeTest.CHARGING_TYPE_02;
import static com.github.charlemaznable.bunny.rabbittest.charge.ChargeTest.CHARGING_TYPE_03;

@Component
public class BunnyDaoChargeImpl implements BunnyDao {

    private int balance00 = 0;
    private int balance01 = 100;

    @Override
    public int updateBalanceByCharge(String chargingType, int chargeValue) {
        if (CHARGING_TYPE_00.equals(chargingType)) {
            balance00 += chargeValue;
        } else if (CHARGING_TYPE_01.equals(chargingType)) {
            balance01 += chargeValue;
        } else if (CHARGING_TYPE_02.equals(chargingType)) {
            return 0;
        } else if (CHARGING_TYPE_03.equals(chargingType)) {
            throw new MockException("Charge Exception");
        }
        return 1;
    }

    @Override
    public QueryResult queryChargingBalance(String chargingType) {
        val queryResult = new QueryResult();
        if (CHARGING_TYPE_00.equals(chargingType)) {
            queryResult.setBalance(balance00);
            queryResult.setUnit("条");
        } else if (CHARGING_TYPE_01.equals(chargingType)) {
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
