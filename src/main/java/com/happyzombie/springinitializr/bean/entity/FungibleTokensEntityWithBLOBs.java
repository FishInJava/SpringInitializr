package com.happyzombie.springinitializr.bean.entity;

public class FungibleTokensEntityWithBLOBs extends FungibleTokensEntity {
    private String spec;

    private String icon;

    public FungibleTokensEntityWithBLOBs(String accountId, String symbol, String name, Integer decimals, String reference, String referenceHash, String spec, String icon) {
        super(accountId, symbol, name, decimals, reference, referenceHash);
        this.spec = spec;
        this.icon = icon;
    }

    public FungibleTokensEntityWithBLOBs() {
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