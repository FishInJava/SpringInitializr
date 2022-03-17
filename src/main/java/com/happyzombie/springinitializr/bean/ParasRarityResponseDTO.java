package com.happyzombie.springinitializr.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
public class ParasRarityResponseDTO {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("data")
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("trait_type")
        private String traitType;
        @JsonProperty("value")
        private String value;
        @JsonProperty("rarity")
        private RarityDTO rarity;

        @NoArgsConstructor
        @Data
        public static class RarityDTO {
            @JsonProperty("count")
            private Integer count;
            @JsonProperty("rarity")
            private BigDecimal rarity;
        }
    }
}
