package com.happyzombie.springinitializr.bean.response.reffinance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.bean.response.NearGeneralResponse;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@NoArgsConstructor
@Data
public class ListUserSeedsResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("block_hash")
        private String blockHash;
        @JsonProperty("block_height")
        private Integer blockHeight;
        @JsonProperty("error")
        private String error;
        @JsonProperty("logs")
        private List<?> logs;
        @JsonProperty("result")
        private byte[] result;

        public Map getResultMap() {
            AssertUtil.shouldBeTrue(result != null && result.length > 0, "result is null!!");
            final String s = new String(result, StandardCharsets.UTF_8);
            return JsonUtil.jsonStringToObject(s, Map.class);
        }
    }


}
