-- near_analyze相关业务数据库
CREATE
DATABASE `near_analyze` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

-- transactions表
CREATE TABLE `near_analyze`.`transactions`
(
    id                        BIGINT auto_increment,
    `hash`                    VARCHAR(128) NOT NULL COMMENT '交易hash',
    `synchronized_account_id` VARCHAR(64)  NOT NULL COMMENT '同步的account-id',
    `signer_account_id`       VARCHAR(64),
    `receiver_account_id`     VARCHAR(64),
    `block_timestamp`         BIGINT COMMENT '交易创建时间戳',
    `included_in_block_hash`  VARCHAR(128),
    `index_in_chunk`          INT,
    PRIMARY KEY (`hash`),
    UNIQUE (id)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='交易信息表,数据来源是near-explorer-backend的transactions-list-by-account-id接口，和near浏览器相同';


-- transaction_actions表
CREATE TABLE `near_analyze`.`transaction_actions`
(
    id                 bigint auto_increment,
    `transaction_hash` VARCHAR(128) NOT NULL COMMENT '交易hash',
    `action_kind`      VARCHAR(40) COMMENT '交易动作',
    `args`             JSON,
    PRIMARY KEY (`id`)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易类型表';


-- 交易分析表（来源最新区块实时查询定时任务）
CREATE TABLE `near_analyze`.`transaction_analyze`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `signer_id`        VARCHAR(64) COMMENT '请求发起者',
    `receiver_id`      VARCHAR(64) COMMENT '请求处理者/合约',
    `actions`          VARCHAR(64),
    `is_simple_action` TINYINT UNSIGNED COMMENT '是否单一aciton，1是,0否 ,对于多action，后续还要优化',
    `method_name`      VARCHAR(64) COMMENT '只适用于action是FunctionCall',
    `chunk_id`         VARCHAR(128) NOT NULL COMMENT '用于追溯该笔交易',
    `create_time`      BIGINT NULL COMMENT '交易时间（要取交易时间，或者区块产生时间）'
        PRIMARY KEY (`id`)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易分析表（来源最新区块）';

-- 同质化代币信息表
CREATE TABLE `near_analyze`.`fungible_tokens`
(
    `account_id`     VARCHAR(128) NOT NULL COMMENT '合约名称',
    `symbol`         VARCHAR(36) COMMENT '合约符号',
    `name`           VARCHAR(128),
    `decimals`       TINYINT UNSIGNED,
    `spec`           LONGTEXT,
    `icon`           LONGTEXT,
    `reference`      VARCHAR(1024),
    `reference_hash` VARCHAR(128),
    PRIMARY KEY (`account_id`)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同质化代币信息表';

-- nft信息表
CREATE TABLE `near_analyze`.`non_fungible_tokens`
(
    `account_id`     VARCHAR(128) NOT NULL COMMENT '合约名称',
    `symbol`         VARCHAR(36) COMMENT '合约符号',
    `name`           VARCHAR(128),
    `spec`           LONGTEXT,
    `icon`           LONGTEXT,
    `base_uri`       VARCHAR(2048),
    `reference`      VARCHAR(1024),
    `reference_hash` VARCHAR(128),
    PRIMARY KEY (`account_id`)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='nft信息表';
