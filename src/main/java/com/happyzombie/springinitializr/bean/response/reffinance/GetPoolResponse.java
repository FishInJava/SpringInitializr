package com.happyzombie.springinitializr.bean.response.reffinance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.bean.response.NearContractResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author admin
 */
@NoArgsConstructor
@Data
public class GetPoolResponse extends NearContractResponse<GetPoolResponse.Pool> {
    @NoArgsConstructor
    @Data
    public static class Pool {
        @JsonProperty("pool_id")
        private Integer poolId;
        @JsonProperty("pool_kind")
        private String poolKind;
        @JsonProperty("token_account_ids")
        private List<String> tokenAccountIds;
        @JsonProperty("amounts")
        private List<String> amounts;
        @JsonProperty("total_fee")
        private Integer totalFee;
        @JsonProperty("shares_total_supply")
        private String sharesTotalSupply;
        @JsonProperty("amp")
        private Integer amp;
        // ref-finance后台参数
        private RefFinancePoolInfo refFinancePoolInfo;
        //
        private String userFarmStake;
        private String userPoolShares;
        // 单位$
        private String userValue;
    }
}
