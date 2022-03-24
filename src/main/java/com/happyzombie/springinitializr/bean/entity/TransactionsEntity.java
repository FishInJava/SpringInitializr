package com.happyzombie.springinitializr.bean.entity;

public class TransactionsEntity {
    private String hash;

    private String signerAccountId;

    private String receiverAccountId;

    private Integer blockTimestamp;

    private String includedInBlockHash;

    private Integer indexInChunk;

    public TransactionsEntity(String hash, String signerAccountId, String receiverAccountId, Integer blockTimestamp, String includedInBlockHash, Integer indexInChunk) {
        this.hash = hash;
        this.signerAccountId = signerAccountId;
        this.receiverAccountId = receiverAccountId;
        this.blockTimestamp = blockTimestamp;
        this.includedInBlockHash = includedInBlockHash;
        this.indexInChunk = indexInChunk;
    }

    public TransactionsEntity() {
        super();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getSignerAccountId() {
        return signerAccountId;
    }

    public void setSignerAccountId(String signerAccountId) {
        this.signerAccountId = signerAccountId == null ? null : signerAccountId.trim();
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(String receiverAccountId) {
        this.receiverAccountId = receiverAccountId == null ? null : receiverAccountId.trim();
    }

    public Integer getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Integer blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getIncludedInBlockHash() {
        return includedInBlockHash;
    }

    public void setIncludedInBlockHash(String includedInBlockHash) {
        this.includedInBlockHash = includedInBlockHash == null ? null : includedInBlockHash.trim();
    }

    public Integer getIndexInChunk() {
        return indexInChunk;
    }

    public void setIndexInChunk(Integer indexInChunk) {
        this.indexInChunk = indexInChunk;
    }
}