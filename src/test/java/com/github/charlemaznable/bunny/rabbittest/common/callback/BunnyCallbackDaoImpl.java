package com.github.charlemaznable.bunny.rabbittest.common.callback;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Component
public class BunnyCallbackDaoImpl implements BunnyCallbackDao {

    private int count = 0;
    private List<CallbackRecord> list0;
    private List<CallbackRecord> list1;
    private List<CallbackRecord> list2;

    public BunnyCallbackDaoImpl() {
        val callbackRecord0 = new CallbackRecord();
        callbackRecord0.setChargingType("chargingType0");
        callbackRecord0.setSeqId("seqId0");
        callbackRecord0.setCallbackUrl("");
        callbackRecord0.setRequestContent("{}");
        list0 = newArrayList(callbackRecord0);
        val callbackRecord1 = new CallbackRecord();
        callbackRecord1.setChargingType("chargingType1");
        callbackRecord1.setSeqId("seqId1");
        callbackRecord1.setCallbackUrl("http://127.0.0.1:9040/callback01");
        callbackRecord1.setRequestContent("{}");
        list1 = newArrayList(callbackRecord1);
        val callbackRecord2 = new CallbackRecord();
        callbackRecord2.setChargingType("chargingType2");
        callbackRecord2.setSeqId("seqId2");
        callbackRecord2.setCallbackUrl("http://127.0.0.1:9040/callback02");
        callbackRecord2.setRequestContent("{}");
        list2 = newArrayList(callbackRecord2);
    }

    @Override
    public int updateCallbackRequest(String chargingType, String seqId, String requestContent) {
        return 1;
    }

    @Override
    public String queryCallbackUrl(String chargingType, String seqId) {
        return null; // ignore
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
        count++;
        if (1 == count) {
            return list0;
        } else if (2 == count) {
            return list1;
        } else if (3 == count) {
            return list2;
        } else {
            return newArrayList();
        }
    }
}
