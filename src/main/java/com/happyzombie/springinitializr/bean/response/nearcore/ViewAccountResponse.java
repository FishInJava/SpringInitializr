package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.bean.response.NearGeneralResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@NoArgsConstructor
@Data
public class ViewAccountResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

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
