package com.happyzombie.springinitializr.bean.Do;

import lombok.Data;

@Data
public class NftInfoDo {
    private Integer id;
    private String collectionId;
    private String nftId;
    private String attributes;
}
