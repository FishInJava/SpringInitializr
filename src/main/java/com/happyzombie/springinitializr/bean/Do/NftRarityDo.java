package com.happyzombie.springinitializr.bean.Do;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NftRarityDo {
    private Integer id;
    private String nftId;
    private String rarity;
    private String antisocialApeClub;
    private Integer antisocialApeClubCount;
    private BigDecimal antisocialApeClubRarity;
    private String skin;
    private Integer skinCount;
    private BigDecimal skinRarity;
    private String mouth;
    private Integer mouthCount;
    private BigDecimal mouthRarity;
    private String eyes;
    private Integer eyesCount;
    private BigDecimal eyesRarity;
    private String head;
    private Integer headCount;
    private BigDecimal headRarity;
    private String neck;
    private Integer neckCount;
    private BigDecimal neckRarity;
}
