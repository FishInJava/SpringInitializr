package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@NoArgsConstructor
@Data
public class TxStatusResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("receipts")
        private List<ReceiptsDTO> receipts;
        @JsonProperty("receipts_outcome")
        private List<ReceiptsOutcomeDTO> receiptsOutcome;
        @JsonProperty("status")
        private StatusDTO status;
        @JsonProperty("transaction")
        private TransactionDTO transaction;
        @JsonProperty("transaction_outcome")
        private TransactionOutcomeDTO transactionOutcome;

        @NoArgsConstructor
        @Data
        public static class StatusDTO {
            @JsonProperty("SuccessValue")
            private String successValue;
        }

        @NoArgsConstructor
        @Data
        public static class TransactionDTO {
            @JsonProperty("actions")
            private List<ActionsDTO> actions;
            @JsonProperty("hash")
            private String hash;
            @JsonProperty("nonce")
            private BigInteger nonce;
            @JsonProperty("public_key")
            private String publicKey;
            @JsonProperty("receiver_id")
            private String receiverId;
            @JsonProperty("signature")
            private String signature;
            @JsonProperty("signer_id")
            private String signerId;

            @NoArgsConstructor
            @Data
            public static class ActionsDTO {
                @JsonProperty("FunctionCall")
                private FunctionCallDTO functionCall;

                @NoArgsConstructor
                @Data
                public static class FunctionCallDTO {
                    @JsonProperty("args")
                    private String args;
                    @JsonProperty("deposit")
                    private String deposit;
                    @JsonProperty("gas")
                    private BigInteger gas;
                    @JsonProperty("method_name")
                    private String methodName;
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class TransactionOutcomeDTO {
            @JsonProperty("block_hash")
            private String blockHash;
            @JsonProperty("id")
            private String id;
            @JsonProperty("outcome")
            private OutcomeDTO outcome;
            @JsonProperty("proof")
            private List<ProofDTO> proof;

            @NoArgsConstructor
            @Data
            public static class OutcomeDTO {
                @JsonProperty("executor_id")
                private String executorId;
                @JsonProperty("gas_burnt")
                private BigInteger gasBurnt;
                @JsonProperty("logs")
                private List<?> logs;
                @JsonProperty("receipt_ids")
                private List<String> receiptIds;
                @JsonProperty("status")
                private StatusDTO status;
                @JsonProperty("tokens_burnt")
                private String tokensBurnt;

                @NoArgsConstructor
                @Data
                public static class StatusDTO {
                    @JsonProperty("SuccessReceiptId")
                    private String successReceiptId;
                }
            }

            @NoArgsConstructor
            @Data
            public static class ProofDTO {
                @JsonProperty("direction")
                private String direction;
                @JsonProperty("hash")
                private String hash;
            }
        }

        @NoArgsConstructor
        @Data
        public static class ReceiptsDTO {
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
                        @JsonProperty("FunctionCall")
                        private FunctionCallDTO functionCall;

                        @NoArgsConstructor
                        @Data
                        public static class FunctionCallDTO {
                            @JsonProperty("args")
                            private String args;
                            @JsonProperty("deposit")
                            private String deposit;
                            @JsonProperty("gas")
                            private BigInteger gas;
                            @JsonProperty("method_name")
                            private String methodName;
                        }
                    }
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class ReceiptsOutcomeDTO {
            @JsonProperty("block_hash")
            private String blockHash;
            @JsonProperty("id")
            private String id;
            @JsonProperty("outcome")
            private OutcomeDTO outcome;
            @JsonProperty("proof")
            private List<ProofDTO> proof;

            @NoArgsConstructor
            @Data
            public static class OutcomeDTO {
                @JsonProperty("executor_id")
                private String executorId;
                @JsonProperty("gas_burnt")
                private BigInteger gasBurnt;
                @JsonProperty("logs")
                private List<String> logs;
                @JsonProperty("receipt_ids")
                private List<String> receiptIds;
                @JsonProperty("status")
                private StatusDTO status;
                @JsonProperty("tokens_burnt")
                private String tokensBurnt;

                @NoArgsConstructor
                @Data
                public static class StatusDTO {
                    @JsonProperty("SuccessReceiptId")
                    private String successReceiptId;
                }
            }

            @NoArgsConstructor
            @Data
            public static class ProofDTO {
                @JsonProperty("direction")
                private String direction;
                @JsonProperty("hash")
                private String hash;
            }
        }
    }
}
