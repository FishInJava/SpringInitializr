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
CREATE TABLE `nft_info` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                            `collection_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'nft账号表中的账号id',
                            `nft_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'nftid',
                            `attributes` text COLLATE utf8mb4_general_ci COMMENT '属性json',
                            `token_series_id` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
                            `media` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片地址',
                            KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='单个nft信息表';


-- nft稀有度表
CREATE TABLE `nft_rarity` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                              `nft_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单个nft信息表中nftid',
                              `rarity` text COLLATE utf8mb4_general_ci COMMENT '稀有度信息',
                              `antisocial_ape_club` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
                              `antisocial_ape_club_count` int(11) DEFAULT NULL,
                              `antisocial_ape_club_rarity` decimal(15,5) DEFAULT NULL,
                              `skin` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '皮肤',
                              `skin_count` int(11) DEFAULT NULL,
                              `skin_rarity` decimal(15,5) DEFAULT NULL,
                              `mouth` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '嘴',
                              `mouth_count` int(11) DEFAULT NULL,
                              `mouth_rarity` decimal(15,5) DEFAULT NULL,
                              `eyes` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '眼',
                              `eyes_count` int(11) DEFAULT NULL,
                              `eyes_rarity` decimal(15,5) DEFAULT NULL,
                              `head` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头',
                              `head_count` int(11) DEFAULT NULL,
                              `head_rarity` decimal(15,5) DEFAULT NULL,
                              `neck` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '脖',
                              `neck_count` int(11) DEFAULT NULL,
                              `neck_rarity` decimal(15,5) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='nft稀有度表';