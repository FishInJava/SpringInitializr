package com.happyzombie.springinitializr.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionPagination {
    private Long endTimestamp;
    private Integer transactionIndex;
}
