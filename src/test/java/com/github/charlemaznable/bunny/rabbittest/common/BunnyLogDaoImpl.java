package com.github.charlemaznable.bunny.rabbittest.common;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import org.springframework.stereotype.Component;

@Component
public class BunnyLogDaoImpl implements BunnyLogDao {

    @Override
    public void log(String logId, String apiId, String logType, String logContent) {
        // ignore
    }
}
