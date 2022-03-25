package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ViewAccountResponse {
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("result")
    private ResultDTO result;
    @JsonProperty("id")
    private String id;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("amount")
        private String amount;
        @JsonProperty("locked")
        private String locked;
        @JsonProperty("code_hash")
        private String codeHash;
        @JsonProperty("storage_usage")
        private Integer storageUsage;
        @JsonProperty("storage_paid_at")
        private Integer storagePaidAt;
        @JsonProperty("block_height")
        private Long blockHeight;
        @JsonProperty("block_hash")
        private String blockHash;
    }
}
