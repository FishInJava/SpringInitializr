package com.happyzombie.springinitializr.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
public class TransactionPagination {
    private Long endTimestamp;
    private Integer transactionIndex;
}
