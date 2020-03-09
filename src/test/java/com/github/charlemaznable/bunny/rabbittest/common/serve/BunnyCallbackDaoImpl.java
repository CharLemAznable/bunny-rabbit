package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Component
public class BunnyCallbackDaoImpl implements BunnyCallbackDao {

    static final String SEQ_ID_00 = "SEQ_ID_00";
    static final String SEQ_ID_01 = "SEQ_ID_01";
    static final String SEQ_ID_02 = "SEQ_ID_02";
    static final String SEQ_ID_03 = "SEQ_ID_03";
    static final String SEQ_ID_04 = "SEQ_ID_04";

    @Override
    public int updateCallbackRequest(String chargingType, String seqId, String requestContent) {
        return 1;
    }

    @Override
    public String queryCallbackUrl(String chargingType, String seqId) {
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
        // empty
    }

    @Override
    public void updateCallbackState(String chargingType, String seqId, String callbackState) {
        // empty
    }

    @Override
    public List<CallbackRecord> queryCallbackRecords() {
        return newArrayList(); // ignore
    }
}
