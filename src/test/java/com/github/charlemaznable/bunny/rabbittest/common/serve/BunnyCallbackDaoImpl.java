package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class BunnyCallbackDaoImpl implements BunnyCallbackDao {

    static final String SEQ_ID_00 = "SEQ_ID_00";
    static final String SEQ_ID_01 = "SEQ_ID_01";
    static final String SEQ_ID_02 = "SEQ_ID_02";
    static final String SEQ_ID_03 = "SEQ_ID_03";
    static final String SEQ_ID_04 = "SEQ_ID_04";

    @Override
    public String queryCallbackUrl(String chargingType, String seqId) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
        if (SEQ_ID_01.equals(seqId)) {
            return "http://127.0.0.1:9030/callback01";
        } else if (SEQ_ID_02.equals(seqId)) {
            return "http://127.0.0.1:9030/callback02";
        } else if (SEQ_ID_03.equals(seqId)) {
            return "http://127.0.0.1:9030/callback03";
        } else if (SEQ_ID_04.equals(seqId)) {
            return "http://127.0.0.1:9030/callback04";
        }
        return null;
    }

    @Override
    public void logCallback(String logId, String seqId, String logType, String logContent) {
        assertNotNull(MtcpContext.getTenantId());
        assertNotNull(MtcpContext.getTenantCode());
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());
    }

    @Override
    public void updateCallbackState(String chargingType, String seqId, String callbackState) {
        assertEquals(chargingType, MtcpContext.getTenantId());
        assertEquals(chargingType, MtcpContext.getTenantCode());
    }
}
