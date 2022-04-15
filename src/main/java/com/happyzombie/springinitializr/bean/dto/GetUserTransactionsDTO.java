package com.happyzombie.springinitializr.bean.dto;

import com.happyzombie.springinitializr.bean.ActionEnum;
import lombok.Data;

import java.util.LinkedList;

/**
 * @author admin
 */
@Data
public class GetUserTransactionsDTO {
    private String hash;

    private String signerAccountId;

    private String receiverAccountId;

    private Long blockTimestamp;

    private String blockTimestampStr;

    private String includedInBlockHash;

    private Integer indexInChunk;

    private String actionKind;

    private String args;

    /**
     * 所有actions，应该是有顺序的
     */
    private LinkedList<ActionEnum.BigAction> actionList = new LinkedList<>();

    /**
     * push放到第一个
     */
    public void pushAction(ActionEnum.BigAction action) {
        actionList.push(action);
    }

    public ActionEnum.BigAction getFirstAction() {
        return actionList.getFirst();
    }

}
