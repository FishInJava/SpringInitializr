package com.happyzombie.springinitializr.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

@Data
@AllArgsConstructor
public class TransactionPagination {
    private BigInteger endTimestamp;
    private Integer transactionIndex;
}
