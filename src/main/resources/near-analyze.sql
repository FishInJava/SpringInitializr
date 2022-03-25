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

