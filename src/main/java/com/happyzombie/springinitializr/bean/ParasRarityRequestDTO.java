package com.happyzombie.springinitializr.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ParasRarityRequestDTO {
    @JsonProperty("collection_id")
    private String collectionId;
    @JsonProperty("attributes")
    private List<AttributesDTO> attributes;
    @JsonProperty("ttl")
    private Integer ttl;

    @NoArgsConstructor
    @Data
    public static class AttributesDTO {
        @JsonProperty("trait_type")
        private String traitType;
        @JsonProperty("value")
        private String value;
    }
}
