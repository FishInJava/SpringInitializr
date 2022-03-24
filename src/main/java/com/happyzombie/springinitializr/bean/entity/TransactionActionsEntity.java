package com.happyzombie.springinitializr.bean.entity;

public class TransactionActionsEntity {
    private String transactionHash;

    private String actionKind;

    private String args;

    public TransactionActionsEntity(String transactionHash, String actionKind) {
        this.transactionHash = transactionHash;
        this.actionKind = actionKind;
    }

    public TransactionActionsEntity(String transactionHash, String actionKind, String args) {
        this.transactionHash = transactionHash;
        this.actionKind = actionKind;
        this.args = args;
    }

    public TransactionActionsEntity() {
        super();
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash == null ? null : transactionHash.trim();
    }

    public String getActionKind() {
        return actionKind;
    }

    public void setActionKind(String actionKind) {
        this.actionKind = actionKind == null ? null : actionKind.trim();
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args == null ? null : args.trim();
    }
}