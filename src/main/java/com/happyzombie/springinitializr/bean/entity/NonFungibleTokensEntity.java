package com.happyzombie.springinitializr.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NonFungibleTokensEntity {
    private String accountId;

    private String symbol;

    private String name;

    private String baseUri;

    private String reference;

    private String referenceHash;
}