package com.happyzombie.springinitializr.bean.entity;

public class FungibleTokensEntity {
    private String accountId;

    private String symbol;

    private String name;

    private Integer decimals;

    private String reference;

    private String referenceHash;

    public FungibleTokensEntity(String accountId, String symbol, String name, Integer decimals, String reference, String referenceHash) {
        this.accountId = accountId;
        this.symbol = symbol;
        this.name = name;
        this.decimals = decimals;
        this.reference = reference;
        this.referenceHash = referenceHash;
    }

    public FungibleTokensEntity() {
        super();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference == null ? null : reference.trim();
    }

    public String getReferenceHash() {
        return referenceHash;
    }

    public void setReferenceHash(String referenceHash) {
        this.referenceHash = referenceHash == null ? null : referenceHash.trim();
    }
}