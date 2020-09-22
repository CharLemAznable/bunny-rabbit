package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.core.miner.MinerConfig;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_CODE;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_ID;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyConfig {

    @MinerConfig(dataId = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();

    @MinerConfig(dataId = "httpserver.context-path", defaultValue = "/bunny")
    String contextPath();

    @MinerConfig(dataId = "httpserver.port", defaultValue = "22114")
    int port();

    @MinerConfig(dataId = "accept.context-keys")
    String acceptContextKeys();

    default List<String> acceptContextKeyList() {
        return newArrayList(Iterables.concat(
                newArrayList(TENANT_ID, TENANT_CODE),
                Splitter.on(",").omitEmptyStrings().trimResults()
                        .splitToList(toStr(acceptContextKeys()))));
    }

    @MinerConfig(dataId = "callback.limit", defaultValue = "3")
    int callbackLimit(); // callback times limit

    @MinerConfig(dataId = "callback.delay", defaultValue = "60000")
    long callbackDelay(); // in MILLISECONDS
}
