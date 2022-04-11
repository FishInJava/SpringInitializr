package com.happyzombie.springinitializr.bean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private String methodName;
    private String realMethodName;
    private String realAction;

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

        private RealArg realArg;

        public void setArgs(String args) {
            if (StringUtil.isNotEmpty(args)) {
                this.args = CompressAndDecompressUtil.base64Decode(args);
                return;
            }
            this.args = args;
        }

        @NoArgsConstructor
        @Data
        public static class RealArg {

            @JsonProperty("request")
            private RequestDTO request;

            @NoArgsConstructor
            @Data
            public static class RequestDTO {
                @JsonProperty("receiver_id")
                private String receiverId;
                @JsonProperty("actions")
                private List<ActionsDTO> actions;

                @NoArgsConstructor
                @Data
                public static class ActionsDTO {
                    @JsonProperty("type")
                    private String type;
                    @JsonProperty("public_key")
                    private String publicKey;
                    @JsonProperty("deposit")
                    private String deposit;
                    @JsonProperty("permission")
                    private PermissionDTO permission;

                    @NoArgsConstructor
                    @Data
                    public static class PermissionDTO {
                        @JsonProperty("receiver_id")
                        private String receiverId;
                        @JsonProperty("allowance")
                        private String allowance;
                        @JsonProperty("method_names")
                        private List<?> methodNames;
                    }
                }
            }
        }


    }

}
