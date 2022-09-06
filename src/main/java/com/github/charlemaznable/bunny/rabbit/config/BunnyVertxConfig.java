package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.configservice.annotation.ConfigValueParse;
import com.github.charlemaznable.configservice.apollo.ApolloConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.github.charlemaznable.configservice.impl.VertxOptionsParser;
import com.hazelcast.config.Config;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.val;

@ApolloConfig
@DiamondConfig
public interface BunnyVertxConfig {

    @ApolloConfig(namespace = "VertxOptions", propertyName = "${bunny-vertx-config:-bunny}",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n" +
                    "clusterManager=@com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig$DefaultBunnyVertxClusterManager\n")
    @DiamondConfig(group = "VertxOptions", dataId = "${bunny-vertx-config:-bunny}",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n" +
                    "clusterManager=@com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig$DefaultBunnyVertxClusterManager\n")
    @ConfigValueParse(VertxOptionsParser.class)
    VertxOptions vertxOptions();

    class DefaultBunnyVertxClusterManager extends HazelcastClusterManager {

        public DefaultBunnyVertxClusterManager() {
            super();
            val hazelcastConfig = new Config();
            hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
            this.setConfig(hazelcastConfig);
        }
    }
}
