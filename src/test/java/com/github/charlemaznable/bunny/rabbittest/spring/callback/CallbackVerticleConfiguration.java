package com.github.charlemaznable.bunny.rabbittest.spring.callback;

import com.github.charlemaznable.bunny.rabbit.spring.BunnyDeployedEvent;
import com.github.charlemaznable.bunny.rabbit.spring.BunnyImport;
import com.github.charlemaznable.bunny.rabbittest.common.callback.CallbackCommon;
import com.github.charlemaznable.bunny.rabbittest.common.common.BunnyEqlerDummy;
import com.github.charlemaznable.core.spring.ComplexComponentScan;
import org.n3r.diamond.client.impl.MockDiamondServer;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.github.charlemaznable.bunny.rabbit.core.verticle.CallbackVerticle.CALLBACK_VERTICLE;
import static com.github.charlemaznable.core.miner.MinerFactory.springMinerLoader;
import static com.github.charlemaznable.core.net.ohclient.OhFactory.springOhLoader;
import static java.util.Objects.nonNull;
import static org.joor.Reflect.on;

@ComplexComponentScan(basePackageClasses = {
        CallbackCommon.class,
        BunnyEqlerDummy.class
})
@BunnyImport
public class CallbackVerticleConfiguration {

    static boolean CALLBACK_DEPLOYED = false;

    @PostConstruct
    public void postConstruct() {
        on(springMinerLoader()).field("minerCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "default",
                "httpserver.port=32115\n" +
                        "callback.delay=1000");
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:32115/bunny\n");
    }

    @PreDestroy
    public void preDestroy() {
        MockDiamondServer.tearDownMockServer();
    }

    @EventListener
    public void bunnyDeployment(BunnyDeployedEvent event) {
        if (CALLBACK_VERTICLE.equals(event.getVerticleName())) {
            CALLBACK_DEPLOYED = nonNull(event.getDeploymentId());
        }
    }
}
