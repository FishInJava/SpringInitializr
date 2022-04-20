package com.happyzombie.springinitializr.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;

/**
 * @author admin
 */
@AllArgsConstructor
@Getter
public enum ActionEnum {
    // "ADD_KEY" "CREATE_ACCOUNT" "DELETE_ACCOUNT" "DELETE_KEY"  "DEPLOY_CONTRACT" "FUNCTION_CALL" "STAKE" "TRANSFER"
    ADD_KEY("ADDKEY", "AddKey", AddKey.class),

    DELETE_KEY("DELETEKEY", "DeleteKey", DeleteKey.class),

    FUNCTION_CALL("FUNCTIONCALL", "FunctionCall", FunctionCall.class),

    TRANSFER("TRANSFER", "Transfer", Transfer.class),

    STAKE("STAKE", "Stake", Stake.class),

    DEPLOY_CONTRACT("DEPLOYCONTRACT", "DeployContract", DeployContract.class),

    CREATE_ACCOUNT("CREATEACCOUNT", "CreateAccount", CreateAccount.class),

    DELETE_ACCOUNT("DELETEACCOUNT", "DeleteAccount", DeleteAccount.class);

    private final String key;
    private final String value;
    private final Class clazz;

    public static ActionEnum getAction(String action) {
        AssertUtil.shouldBeTrue(StringUtil.isNotEmpty(action), "action 不能为空");
        final String str = action.replaceAll("_", "").toUpperCase(Locale.ROOT);
        for (ActionEnum value : ActionEnum.values()) {
            if (value.getKey().equals(str)) {
                return value;
            }
        }
        throw new RuntimeException("未匹配Action" + str);
    }

    @NoArgsConstructor
    @Data
    public static class BigAction {
        @JsonProperty("type")
        private String type;
        @JsonProperty("gas")
        private Long gas;
        @JsonProperty("args")
        private String args;
        @JsonProperty("deposit")
        private String deposit;
        // 目前Transfer中有该参数
        private String amount;
        @JsonProperty("method_name")
        private String methodName;
        @JsonProperty("access_key")
        private String accessKey;
        @JsonProperty("public_key")
        private String publicKey;
        @JsonProperty("stake")
        private String stake;
        private String receiverId;
        /**
         * 自合约中confirm和delete_request方法会填充该值
         */
        @JsonProperty("request_id")
        private Long requestId;
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

        public void setArgsDirect(String args) {
            this.args = args;
        }

        public void setArgs(String args) {
            if (StringUtil.isNotEmpty(args)) {
                this.args = CompressAndDecompressUtil.base64Decode(args);
                return;
            }
            this.args = args;
        }
    }

    @NoArgsConstructor
    @Data
    public static class FunctionCall {
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

        /**
         * confirm的请求参数
         */
        @NoArgsConstructor
        @Data
        public static class ConfirmRequest {
            @JsonProperty("request_id")
            private Long requestId;
        }

        /**
         * delete_request的请求参数
         */
        @NoArgsConstructor
        @Data
        public static class DeleteRequest {
            @JsonProperty("request_id")
            private Long requestId;
        }

        /**
         * add_request_and_confirm的请求参数
         */
        @NoArgsConstructor
        @Data
        public static class AddRequestAndConfirmRequest {
            @JsonProperty("request")
            private RequestDTO request;

            @NoArgsConstructor
            @Data
            public static class RequestDTO {
                @JsonProperty("receiver_id")
                private String receiverId;
                @JsonProperty("actions")
                private List<BigAction> actions;
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class DeleteKey {
        @JsonProperty("public_key")
        private String publicKey;
    }

    @NoArgsConstructor
    @Data
    public static class AddKey {
        @JsonProperty("access_key")
        private String accessKey;
        @JsonProperty("public_key")
        private String publicKey;
    }

    @NoArgsConstructor
    @Data
    public static class Stake {
        @JsonProperty("stake")
        private String stake;
        @JsonProperty("public_key")
        private String publicKey;
    }

    @NoArgsConstructor
    @Data
    public static class Transfer {
        @JsonProperty("deposit")
        private String deposit;
    }

    @NoArgsConstructor
    @Data
    public static class DeployContract {

    }

    @NoArgsConstructor
    @Data
    public static class CreateAccount {

    }

    @NoArgsConstructor
    @Data
    public static class DeleteAccount {
        @JsonProperty("beneficiary_id")
        private String beneficiaryId;
    }


}
