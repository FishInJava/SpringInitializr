package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ReceiptDetailsResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("predecessor_id")
        private String predecessorId;
        @JsonProperty("receipt")
        private ReceiptDTO receipt;
        @JsonProperty("receipt_id")
        private String receiptId;
        @JsonProperty("receiver_id")
        private String receiverId;

        @NoArgsConstructor
        @Data
        public static class ReceiptDTO {
            @JsonProperty("Action")
            private ActionDTO action;

            @NoArgsConstructor
            @Data
            public static class ActionDTO {
                @JsonProperty("actions")
                private List<ActionsDTO> actions;
                @JsonProperty("gas_price")
                private String gasPrice;
                @JsonProperty("input_data_ids")
                private List<?> inputDataIds;
                @JsonProperty("output_data_receivers")
                private List<?> outputDataReceivers;
                @JsonProperty("signer_id")
                private String signerId;
                @JsonProperty("signer_public_key")
                private String signerPublicKey;

                @NoArgsConstructor
                @Data
                public static class ActionsDTO {
                    @JsonProperty("Transfer")
                    private TransferDTO transfer;

                    @NoArgsConstructor
                    @Data
                    public static class TransferDTO {
                        @JsonProperty("deposit")
                        private String deposit;
                    }
                }
            }
        }
    }
}
