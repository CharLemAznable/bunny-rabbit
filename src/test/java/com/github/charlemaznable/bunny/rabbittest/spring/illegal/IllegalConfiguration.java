package com.github.charlemaznable.bunny.rabbittest.spring.illegal;

import com.github.charlemaznable.bunny.rabbit.spring.BunnyDeployedEvent;
import com.github.charlemaznable.bunny.rabbit.spring.BunnyImport;
import com.github.charlemaznable.bunny.rabbit.vertx.BunnyVertxImport;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyEqlerDummy;
import com.github.charlemaznable.bunny.rabbittest.common.illegal.IllegalCommon;
import com.github.charlemaznable.core.spring.NeoComponentScan;
import com.github.charlemaznable.miner.MinerScan;
import org.n3r.diamond.client.impl.MockDiamondServer;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.github.charlemaznable.bunny.rabbit.core.verticle.EventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.core.verticle.HttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.httpclient.ohclient.OhFactory.springOhLoader;
import static com.github.charlemaznable.miner.MinerFactory.springMinerLoader;
import static java.util.Objects.nonNull;
import static org.n3r.eql.joor.Reflect.on;

@NeoComponentScan(basePackageClasses = {
        IllegalCommon.class,
        BunnyEqlerDummy.class
})
@BunnyImport
@BunnyVertxImport
@MinerScan(basePackageClasses = {
        IllegalCommon.class
})
public final class IllegalConfiguration {

    static boolean EVENT_BUS_DEPLOYED = false;
    static boolean HTTP_SERVER_DEPLOYED = false;

    @PostConstruct
    public void postConstruct() {
        on(springMinerLoader()).field("minerCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "illegal",
                "httpserver.port=32117\n" +
                        "httpserver.context-path=\n" +
                        "eventbus.address-prefix=\n");
    }

    @PreDestroy
    public void preDestroy() {
        MockDiamondServer.tearDownMockServer();
    }

    @EventListener
    public void bunnyDeployment(BunnyDeployedEvent event) {
        if (EVENT_BUS_VERTICLE.equals(event.getVerticleName())) {
            EVENT_BUS_DEPLOYED = nonNull(event.getDeploymentId());
        } else if (HTTP_SERVER_VERTICLE.equals(event.getVerticleName())) {
            HTTP_SERVER_DEPLOYED = nonNull(event.getDeploymentId());
        }
    }
}
