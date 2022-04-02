-- near_analyze相关业务数据库
CREATE
DATABASE `near_analyze` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

-- transactions表

CREATE TABLE `near_analyze`.`transactions`
(
    id                       BIGINT auto_increment,
    `hash`                   VARCHAR(128) NOT NULL COMMENT '交易hash',
    `signer_account_id`      VARCHAR(64),
    `receiver_account_id`    VARCHAR(64),
    `block_timestamp`        BIGINT COMMENT '交易创建时间戳',
    `included_in_block_hash` VARCHAR(128),
    `index_in_chunk`         INT,
    PRIMARY KEY (`hash`),
    UNIQUE (id)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交易信息表';


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