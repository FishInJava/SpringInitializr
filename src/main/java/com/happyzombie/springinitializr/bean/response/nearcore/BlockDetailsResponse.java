package com.happyzombie.springinitializr.bean.response.nearcore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BlockDetailsResponse extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("author")
        private String author;
        @JsonProperty("header")
        private HeaderDTO header;
        @JsonProperty("chunks")
        private List<ChunksDTO> chunks;

        @NoArgsConstructor
        @Data
        public static class HeaderDTO {
            @JsonProperty("height")
            private Long height;
            @JsonProperty("epoch_id")
            private String epochId;
            @JsonProperty("next_epoch_id")
            private String nextEpochId;
            @JsonProperty("hash")
            private String hash;
            @JsonProperty("prev_hash")
            private String prevHash;
            @JsonProperty("prev_state_root")
            private String prevStateRoot;
            @JsonProperty("chunk_receipts_root")
            private String chunkReceiptsRoot;
            @JsonProperty("chunk_headers_root")
            private String chunkHeadersRoot;
            @JsonProperty("chunk_tx_root")
            private String chunkTxRoot;
            @JsonProperty("outcome_root")
            private String outcomeRoot;
            @JsonProperty("chunks_included")
            private Long chunksIncluded;
            @JsonProperty("challenges_root")
            private String challengesRoot;
            @JsonProperty("timestamp")
            private Long timestamp;
            @JsonProperty("timestamp_nanosec")
            private String timestampNanosec;
            @JsonProperty("random_value")
            private String randomValue;
            @JsonProperty("validator_proposals")
            private List<?> validatorProposals;
            @JsonProperty("chunk_mask")
            private List<Boolean> chunkMask;
            @JsonProperty("gas_price")
            private String gasPrice;
            @JsonProperty("rent_paid")
            private String rentPaid;
            @JsonProperty("validator_reward")
            private String validatorReward;
            @JsonProperty("total_supply")
            private String totalSupply;
            @JsonProperty("challenges_result")
            private List<?> challengesResult;
            @JsonProperty("last_final_block")
            private String lastFinalBlock;
            @JsonProperty("last_ds_final_block")
            private String lastDsFinalBlock;
            @JsonProperty("next_bp_hash")
            private String nextBpHash;
            @JsonProperty("block_merkle_root")
            private String blockMerkleRoot;
            @JsonProperty("approvals")
            private List<String> approvals;
            @JsonProperty("signature")
            private String signature;
            @JsonProperty("latest_protocol_version")
            private Long latestProtocolVersion;
        }

        @NoArgsConstructor
        @Data
        public static class ChunksDTO {
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
    }
}
