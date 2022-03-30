package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ChunkDetailsResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("author")
        private String author;
        @JsonProperty("header")
        private HeaderDTO header;
        @JsonProperty("transactions")
        private List<TransactionsDTO> transactions;
        @JsonProperty("receipts")
        private List<ReceiptsDTO> receipts;

        @NoArgsConstructor
        @Data
        public static class HeaderDTO {
            @JsonProperty("chunk_hash")
            private String chunkHash;
            @JsonProperty("prev_block_hash")
            private String prevBlockHash;
            @JsonProperty("outcome_root")
            private String outcomeRoot;
            @JsonProperty("prev_state_root")
            private String prevStateRoot;
            @JsonProperty("encoded_merkle_root")
            private String encodedMerkleRoot;
            @JsonProperty("encoded_length")
            private Long encodedLength;
            @JsonProperty("height_created")
            private Long heightCreated;
            @JsonProperty("height_included")
            private Long heightIncluded;
            @JsonProperty("shard_id")
            private Long shardId;
            @JsonProperty("gas_used")
            private Long gasUsed;
            @JsonProperty("gas_limit")
            private Long gasLimit;
            @JsonProperty("rent_paid")
            private String rentPaid;
            @JsonProperty("validator_reward")
            private String validatorReward;
            @JsonProperty("balance_burnt")
            private String balanceBurnt;
            @JsonProperty("outgoing_receipts_root")
            private String outgoingReceiptsRoot;
            @JsonProperty("tx_root")
            private String txRoot;
            @JsonProperty("validator_proposals")
            private List<?> validatorProposals;
            @JsonProperty("signature")
            private String signature;
        }

        @NoArgsConstructor
        @Data
        public static class TransactionsDTO {
            @JsonProperty("actions")
            private List<ActionsDTO> actions;
            @JsonProperty("hash")
            private String hash;
            @JsonProperty("nonce")
            private Long nonce;
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
                @JsonProperty("Transfer")
                private Transfer transfer;

                @JsonProperty("FunctionCall")
                private FunctionCallDTO functionCall;

                @NoArgsConstructor
                @Data
                public static class Transfer {
                    @JsonProperty("deposit")
                    private String deposit;
                }

                @NoArgsConstructor
                @Data
                public static class FunctionCallDTO {
                    @JsonProperty("args")
                    private String args;
                    @JsonProperty("deposit")
                    private String deposit;
                    @JsonProperty("gas")
                    private Long gas;
                    @JsonProperty("method_name")
                    private String methodName;
                }
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
                            private Long gas;
                            @JsonProperty("method_name")
                            private String methodName;
                        }
                    }
                }
            }
        }
    }
}
