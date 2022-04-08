package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.List;

@NoArgsConstructor
@Data
public class FTMetadataResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("block_hash")
        private String blockHash;
        @JsonProperty("block_height")
        private Integer blockHeight;
        @JsonProperty("logs")
        private List<?> logs;
        // 这里返回的是byte数组
        @JsonProperty("result")
        private byte[] result;

        @NoArgsConstructor
        @Data
        public static class FTMetadata {
            @JsonProperty("spec")
            private String spec;
            @JsonProperty("name")
            private String name;
            @JsonProperty("symbol")
            private String symbol;
            @JsonProperty("icon")
            private String icon;
            @JsonProperty("reference")
            private String reference;
            @JsonProperty("reference_hash")
            private String referenceHash;
            @JsonProperty("decimals")
            private Integer decimals;
        }

        public FTMetadata getMetadata() {
            AssertUtil.shouldBeTrue(result != null && result.length > 0, "result is null!!");
            final String s = new String(result, StandardCharsets.UTF_8);
            return JsonUtil.jsonStringToObject(s, FTMetadata.class);
        }
    }
}