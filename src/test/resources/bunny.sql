-- noinspection SqlDialectInspectionForFile
-- noinspection SqlNoDataSourceInspectionForFile

DROP TABLE IF EXISTS `SERVICE_ACCOUNT`;

CREATE TABLE `SERVICE_ACCOUNT` (
  `SERVICE_CODE` CHAR(2) NOT NULL COMMENT '服务代码',
  `SERVICE_NAME` VARCHAR(32) NOT NULL COMMENT '服务名称',
  `SERVICE_BALANCE` INT UNSIGNED NOT NULL COMMENT '服务可用余额',
  `SERVICE_UNIT` VARCHAR(32) NOT NULL COMMENT '服务计量单位',
  `TIME_UPDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `REMARK` VARCHAR(1000) NULL COMMENT '备注',
  PRIMARY KEY (`SERVICE_CODE`)
) COMMENT = '服务余额表';


DROP TABLE IF EXISTS `SERVICE_ACCOUNT_SEQ`;

CREATE TABLE `SERVICE_ACCOUNT_SEQ` (
  `SEQ_ID` BIGINT UNSIGNED NOT NULL COMMENT '服务流水号',
  `SERVICE_CODE` CHAR(2) NOT NULL COMMENT '服务代码',
  `SERVICE_USED` INT UNSIGNED NOT NULL COMMENT '服务使用量',
  `SERVICE_UNIT` VARCHAR(32) NOT NULL COMMENT '服务计量单位',
  `SERVICE_STATE` CHAR(1) NOT NULL DEFAULT '0' COMMENT '记账状态, 0-预扣减 1-确认扣减 2-取消扣减',
  `CALLBACK_URL` VARCHAR(200) NULL COMMENT '回调地址',
  `CALLBACK_STATE` CHAR(1) NOT NULL DEFAULT '0' COMMENT '回调状态, 0-初始化 1-待回调 2-回调成功 3-回调失败',
  `CALLBACK_COUNT` TINYINT NOT NULL DEFAULT 0 COMMENT '回调次数',
  `CALLBACK_REQ` TEXT COMMENT '回调请求内容',
  `TIME_UPDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`SEQ_ID`)
) COMMENT = '服务记账表';


DROP TABLE IF EXISTS `SERVICE_ACCOUNT_ERROR_LOG`;

CREATE TABLE `SERVICE_ACCOUNT_ERROR_LOG` (
  `LOG_ID` BIGINT UNSIGNED NOT NULL COMMENT '异常日志流水号',
  `SEQ_ID` BIGINT UNSIGNED NOT NULL COMMENT '服务流水号',
  `SERVICE_CODE` CHAR(2) NOT NULL COMMENT '服务代码',
  `ERROR_CONTENT` TEXT COMMENT '异常内容',
  `ERROR_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`LOG_ID`)
) COMMENT = '服务异常日志表';


DROP TABLE IF EXISTS `SERVICE_API_LOG`;

CREATE TABLE `SERVICE_API_LOG` (
  `LOG_ID` BIGINT UNSIGNED NOT NULL COMMENT '日志流水号',
  `API_ID` BIGINT UNSIGNED NOT NULL COMMENT '服务接口标识',
  `LOG_TYPE` VARCHAR(20) NOT NULL COMMENT '日志类型',
  `LOG_CONTENT` TEXT COMMENT '日志内容',
  `LOG_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间',
  PRIMARY KEY (`LOG_ID`)
) COMMENT = '服务接口日志表';


DROP TABLE IF EXISTS `SERVICE_CALLBACK_LOG`;

CREATE TABLE `SERVICE_CALLBACK_LOG` (
  `LOG_ID` BIGINT UNSIGNED NOT NULL COMMENT '日志流水号',
  `SEQ_ID` BIGINT UNSIGNED NOT NULL COMMENT '服务流水号',
  `LOG_TYPE` VARCHAR(20) NOT NULL COMMENT '日志类型',
  `LOG_CONTENT` TEXT COMMENT '日志内容',
  `LOG_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间',
  PRIMARY KEY (`LOG_ID`)
) COMMENT = '服务接口日志表';
