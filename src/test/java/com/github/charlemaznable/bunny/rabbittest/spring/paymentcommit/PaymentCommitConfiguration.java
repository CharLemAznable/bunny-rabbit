package com.github.charlemaznable.bunny.rabbittest.spring.paymentcommit;

import com.github.charlemaznable.bunny.rabbit.spring.BunnyImport;
import com.github.charlemaznable.bunny.rabbit.spring.BunnyVerticleDeploymentEvent;
import com.github.charlemaznable.core.spring.ComplexComponentScan;
import org.n3r.diamond.client.impl.MockDiamondServer;
import org.n3r.eql.diamond.Dql;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.core.miner.MinerFactory.springMinerLoader;
import static com.github.charlemaznable.core.net.ohclient.OhFactory.springOhLoader;
import static java.util.Objects.nonNull;
import static org.joor.Reflect.on;

@ComplexComponentScan
@BunnyImport
public class PaymentCommitConfiguration {

    static boolean EVENT_BUS_DEPLOYED = false;
    static boolean HTTP_SERVER_DEPLOYED = false;

    @PostConstruct
    public void postConstruct() {
        on(springMinerLoader()).field("minerCache").call("invalidateAll");
        on(springOhLoader()).field("ohCache").call("invalidateAll");
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo("Bunny", "default",
                "deploy.port=12118");
        MockDiamondServer.setConfigInfo("BunnyClient", "default",
                "httpServerBaseUrl=http://127.0.0.1:12118/bunny");
        MockDiamondServer.setConfigInfo("EqlConfig", "bunny",
                "driver=org.h2.Driver\n" +
                        "url=jdbc:h2:mem:message;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE\n" +
                        "user=\n" +
                        "password=\n");

        new Dql("bunny").execute("" +
                "DROP TABLE IF EXISTS `SERVICE_ACCOUNT`;\n" +
                "\n" +
                "CREATE TABLE `SERVICE_ACCOUNT` (\n" +
                "  `SERVICE_CODE` CHAR(2) NOT NULL COMMENT '服务代码',\n" +
                "  `SERVICE_NAME` VARCHAR(32) NOT NULL COMMENT '服务名称',\n" +
                "  `SERVICE_BALANCE` INT UNSIGNED NOT NULL COMMENT '服务可用余额',\n" +
                "  `SERVICE_UNIT` VARCHAR(32) NOT NULL COMMENT '服务计量单位',\n" +
                "  `TIME_UPDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  `REMARK` VARCHAR(1000) NULL COMMENT '备注',\n" +
                "  PRIMARY KEY (`SERVICE_CODE`)\n" +
                ") COMMENT = '服务余额表';\n" +
                "\n" +
                "INSERT INTO `SERVICE_ACCOUNT`(SERVICE_CODE, SERVICE_NAME, SERVICE_BALANCE, SERVICE_UNIT) values\n" +
                "('00', '短信', 1, '条');\n" +
                "\n" +
                "DROP TABLE IF EXISTS `SERVICE_ACCOUNT_SEQ`;\n" +
                "\n" +
                "CREATE TABLE `SERVICE_ACCOUNT_SEQ` (\n" +
                "  `SEQ_ID` BIGINT UNSIGNED NOT NULL COMMENT '服务流水号',\n" +
                "  `SERVICE_CODE` CHAR(2) NOT NULL COMMENT '服务代码',\n" +
                "  `SERVICE_USED` INT UNSIGNED NOT NULL COMMENT '服务使用量',\n" +
                "  `SERVICE_UNIT` VARCHAR(32) NOT NULL COMMENT '服务计量单位',\n" +
                "  `STATE` CHAR(1) NOT NULL DEFAULT '0' COMMENT '服务使用状态, 0-预扣减 1-确认扣减 2-取消扣减',\n" +
                "  `TIME_UPDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`SEQ_ID`)\n" +
                ") COMMENT = '服务记账表';\n" +
                "\n" +
                "INSERT INTO `SERVICE_ACCOUNT_SEQ`(SEQ_ID, SERVICE_CODE, SERVICE_USED, SERVICE_UNIT) values\n" +
                "(100, '00', 1, '条');\n" +
                "INSERT INTO `SERVICE_ACCOUNT_SEQ`(SEQ_ID, SERVICE_CODE, SERVICE_USED, SERVICE_UNIT) values\n" +
                "(200, '00', 1, '条');\n");
    }

    @PreDestroy
    public void preDestroy() {
        MockDiamondServer.tearDownMockServer();
    }

    @EventListener
    public void bunnyDeployment(BunnyVerticleDeploymentEvent event) {
        if (EVENT_BUS_VERTICLE.equals(event.getVerticleName())) {
            EVENT_BUS_DEPLOYED = nonNull(event.getDeploymentId());
        } else if (HTTP_SERVER_VERTICLE.equals(event.getVerticleName())) {
            HTTP_SERVER_DEPLOYED = nonNull(event.getDeploymentId());
        }
    }
}
