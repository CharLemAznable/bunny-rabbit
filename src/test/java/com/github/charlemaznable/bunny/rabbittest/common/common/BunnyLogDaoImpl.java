package com.github.charlemaznable.bunny.rabbittest.common.common;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class BunnyLogDaoImpl implements BunnyLogDao {

    @Override
    public void log(String logId, String apiId, String logType, String logContent) {
        assertNotNull(MtcpContext.getTenantId());
        assertNotNull(MtcpContext.getTenantCode());
        assertEquals(MtcpContext.getTenantId(), MtcpContext.getTenantCode());
    }
}
