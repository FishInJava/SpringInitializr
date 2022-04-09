package com.happyzombie.springinitializr.bean.response.reffinance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author admin
 */
@NoArgsConstructor
@Data
public class RefFinancePoolInfo {
    @JsonProperty("amounts")
    private List<String> amounts;
    @JsonProperty("amp")
    private Integer amp;
    @JsonProperty("farming")
    private Boolean farming;
    @JsonProperty("id")
    private String id;
    @JsonProperty("pool_kind")
    private String poolKind;
    @JsonProperty("shares_total_supply")
    private String sharesTotalSupply;
    @JsonProperty("token_account_ids")
    private List<String> tokenAccountIds;
    @JsonProperty("token_symbols")
    private List<String> tokenSymbols;
    @JsonProperty("total_fee")
    private Integer totalFee;
    @JsonProperty("tvl")
    private String tvl;
}
