-- paras相关业务数据库
CREATE DATABASE `paras` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

-- nft信息表
create table nft_collection
(
    id            bigint auto_increment,
    collection_id varchar(10) not null comment 'near账号，创建者账号',
    constraint nft_collection_pk
        primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- 单个nft信息表
CREATE TABLE `paras`.`nft_info`(
                                   `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                   `collection_id` VARCHAR(10) DEFAULT '10' COMMENT 'nft账号表中的账号id',
                                   `nft_id` VARCHAR(10) DEFAULT '10' COMMENT 'nftid',
                                   `attributes` TEXT COMMENT '属性json',
                                   KEY(`id`)
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='单个nft信息表';


-- nft稀有度表
