package com.happyzombie.springinitializr.bean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Args argsDTO;

    @NoArgsConstructor
    @Data
    public static class Args {
        @JsonProperty("gas")
        private Long gas;
        @JsonProperty("args")
        private String args;
        @JsonProperty("deposit")
        private String deposit;
        @JsonProperty("method_name")
        private String methodName;

        public void setArgs(String args) {
            if (StringUtil.isNotEmpty(args)) {
                this.args = CompressAndDecompressUtil.base64Decode(args);
                return;
            }
            this.args = args;
        }
    }

}
