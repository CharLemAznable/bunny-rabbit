package com.github.charlemaznable.bunny.rabbit.dao;

import org.n3r.eql.eqler.annotations.Param;

@BunnyEqler
public interface BunnyLogDao {

    void log(@Param("logId") String logId,
             @Param("apiId") String apiId,
             @Param("logType") String logType,
             @Param("logContent") String logContent);
}
