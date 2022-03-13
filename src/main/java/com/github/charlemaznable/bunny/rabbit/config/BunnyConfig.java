package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.configservice.apollo.ApolloConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_CODE;
import static org.n3r.eql.mtcp.MtcpContext.TENANT_ID;

@ApolloConfig(namespace = "Bunny", propertyName = "${bunny-config:-default}")
@DiamondConfig(group = "Bunny", dataId = "${bunny-config:-default}")
public interface BunnyConfig {

    @ApolloConfig(propertyName = "eventbus.address-prefix", defaultValue = "/bunny")
    @DiamondConfig(dataId = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();

    @ApolloConfig(propertyName = "httpserver.context-path", defaultValue = "/bunny")
    @DiamondConfig(dataId = "httpserver.context-path", defaultValue = "/bunny")
    String contextPath();

    @ApolloConfig(propertyName = "httpserver.port", defaultValue = "22114")
    @DiamondConfig(dataId = "httpserver.port", defaultValue = "22114")
    int port();

    @ApolloConfig(propertyName = "accept.context-keys")
    @DiamondConfig(dataId = "accept.context-keys")
    String acceptContextKeys();

    default List<String> acceptContextKeyList() {
        return newArrayList(Iterables.concat(
                newArrayList(TENANT_ID, TENANT_CODE),
                Splitter.on(",").omitEmptyStrings().trimResults()
                        .splitToList(toStr(acceptContextKeys()))));
    }

    @ApolloConfig(propertyName = "callback.limit", defaultValue = "3")
    @DiamondConfig(dataId = "callback.limit", defaultValue = "3")
    int callbackLimit(); // callback times limit

    @ApolloConfig(propertyName = "callback.delay", defaultValue = "60000")
    @DiamondConfig(dataId = "callback.delay", defaultValue = "60000")
    long callbackDelay(); // in MILLISECONDS
}
