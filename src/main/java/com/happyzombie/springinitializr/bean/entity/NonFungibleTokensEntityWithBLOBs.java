package com.happyzombie.springinitializr.bean.entity;

public class NonFungibleTokensEntityWithBLOBs extends NonFungibleTokensEntity {
    private String spec;

    private String icon;

    public NonFungibleTokensEntityWithBLOBs(String accountId, String symbol, String name, String baseUri, String reference, String referenceHash, String spec, String icon) {
        super(accountId, symbol, name, baseUri, reference, referenceHash);
        this.spec = spec;
        this.icon = icon;
    }

    public NonFungibleTokensEntityWithBLOBs() {
        super();
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec == null ? null : spec.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }
}