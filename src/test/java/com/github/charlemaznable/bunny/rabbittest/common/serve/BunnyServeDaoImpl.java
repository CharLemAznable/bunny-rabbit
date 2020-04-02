package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class BunnyServeDaoImpl implements BunnyServeDao {

    static final String UPDATE_BALANCE_ERROR = "UPDATE_BALANCE_ERROR";
    static final String UPDATE_BALANCE_FAILURE = "UPDATE_BALANCE_FAILURE";
    static final String CREATE_SEQ_FAILURE = "CREATE_SEQ_FAILURE";
    static final String CONFIRMED_SEQ_SUCCESS = "CONFIRMED_SEQ_SUCCESS";
    static final String CONFIRMED_SEQ_FAILURE = "CONFIRMED_SEQ_FAILURE";
    static final String CONFIRM_FAILURE = "CONFIRM_FAILURE";
    static final String UPDATE_CONFIRM_FAILURE = "UPDATE_CONFIRM_FAILURE";
    private static final String CHARGE_CODE = "serve.ChargeCode";

    @Override
    public int updateBalanceByPayment(String chargeCode, int paymentValue) {
        assertEquals(CHARGE_CODE, chargeCode);
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (UPDATE_BALANCE_ERROR.equals(MtcpContext.getTenantId())) {
            throw new MockException("Serve Exception");
        } else if (UPDATE_BALANCE_FAILURE.equals(MtcpContext.getTenantId())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int createPreserveSequence(String chargeCode, int paymentValue,
                                      String callbackUrl, String seqId) {
        assertEquals(CHARGE_CODE, chargeCode);
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (CREATE_SEQ_FAILURE.equals(MtcpContext.getTenantId())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String queryConfirmedSequence(String chargeCode,
                                         String seqId) {
        assertEquals(CHARGE_CODE, chargeCode);
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (CONFIRMED_SEQ_SUCCESS.equals(MtcpContext.getTenantId())) {
            return seqId;
        } else if (CONFIRMED_SEQ_FAILURE.equals(MtcpContext.getTenantId())) {
            throw new MockException("Serve Confirm Exception");
        } else {
            return null;
        }
    }

    @Override
    public int confirmPreserveSequence(String chargeCode,
                                       String seqId,
                                       int confirmValue) {
        assertEquals(CHARGE_CODE, chargeCode);
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (CONFIRM_FAILURE.equals(MtcpContext.getTenantId())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int updateBalanceByConfirm(String chargeCode,
                                      String seqId) {
        assertEquals(CHARGE_CODE, chargeCode);
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());

        if (UPDATE_CONFIRM_FAILURE.equals(MtcpContext.getTenantId())) {
            return 0;
        } else {
            return 1;
        }
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
