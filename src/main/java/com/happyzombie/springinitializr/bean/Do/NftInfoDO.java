package com.happyzombie.springinitializr.bean.Do;

import lombok.Data;

@Data
public class NftInfoDO {
    private Integer id;
    private String contractId;
    private String nftId;
    private String attributes;
    private String tokenSeriesId;
    private String name;
    private String media;
}
