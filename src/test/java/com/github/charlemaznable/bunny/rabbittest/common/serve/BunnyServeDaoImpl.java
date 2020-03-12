package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class BunnyServeDaoImpl implements BunnyServeDao {

    static final String CHARGING_TYPE_00 = "00";
    static final String CHARGING_TYPE_01 = "01";
    static final String CHARGING_TYPE_02 = "02";
    static final String CHARGING_TYPE_03 = "03";
    static final String CHARGING_TYPE_04 = "04";
    static final String CHARGING_TYPE_05 = "05";
    static final String CHARGING_TYPE_06 = "06";
    static final String CHARGING_TYPE_07 = "07";

    @Override
    public int updateBalanceByPayment(String chargingType, int paymentValue) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_00.equals(chargingType)) {
            if (1 == paymentValue) return 1;
            else if (2 == paymentValue) return 0;
            else if (3 == paymentValue) return 1;
            else if (4 == paymentValue) throw new MockException("Serve Exception");
        }
        return 1;
    }

    @Override
    public int createPreserveSequence(String chargingType, int paymentValue,
                                      String callbackUrl, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_00.equals(chargingType)) {
            if (1 == paymentValue) return 1;
            else if (2 == paymentValue) return 1; // unreachable
            else if (3 == paymentValue) return 0;
        }
        return 1;
    }

    @Override
    public String queryRollbackedSequence(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_01.equals(chargingType)) {
            return seqId;
        } else if (CHARGING_TYPE_04.equals(chargingType)) {
            throw new MockException("Serve Rollback Exception");
        }
        return null;
    }

    @Override
    public int rollbackPreserveSequence(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_02.equals(chargingType)) {
            return 0;
        }
        return 1;
    }

    @Override
    public int updateBalanceByRollback(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_03.equals(chargingType)) {
            return 0;
        }
        return 1;
    }

    @Override
    public String queryCommitedSequence(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_05.equals(chargingType)) {
            return seqId;
        } else if (CHARGING_TYPE_07.equals(chargingType)) {
            throw new MockException("Serve Commit Exception");
        }
        return null;
    }

    @Override
    public int commitPreserveSequence(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (CHARGING_TYPE_06.equals(chargingType)) {
            return 0;
        }
        return 1;
    }

    @Override
    public void start() {
        // mock
    }

    @Override
    public void commit() {
        // mock
    }

    @Override
    public void rollback() {
        // mock
    }

    @Override
    public void close() {
        // mock
    }
}
