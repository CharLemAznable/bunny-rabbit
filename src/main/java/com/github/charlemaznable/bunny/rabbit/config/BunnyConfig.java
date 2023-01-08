package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.configservice.Config;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_CODE;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_ID;

@Config(keyset = "Bunny", key = "${bunny-config:-default}")
public interface BunnyConfig {

    @Config(key = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();

    @Config(key = "httpserver.context-path", defaultValue = "/bunny")
    String contextPath();

    @Config(key = "httpserver.port", defaultValue = "22114")
    int port();

    @Config("accept.context-keys")
    String acceptContextKeys();

    default List<String> acceptContextKeyList() {
        return newArrayList(Iterables.concat(
                newArrayList(TENANT_ID, TENANT_CODE),
                Splitter.on(",").omitEmptyStrings().trimResults()
                        .splitToList(toStr(acceptContextKeys()))));
    }

    @Config(key = "callback.limit", defaultValue = "3")
    int callbackLimit(); // callback times limit

    @Config(key = "callback.delay", defaultValue = "60000")
    long callbackDelay(); // in MILLISECONDS
}
