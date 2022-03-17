package com.happyzombie.springinitializr.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ParasAttributesDTO {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("results")
        private List<ResultsDTO> results;
        @JsonProperty("skip")
        private Integer skip;
        @JsonProperty("limit")
        private Integer limit;

        @NoArgsConstructor
        @Data
        public static class ResultsDTO {
            @JsonProperty("_id")
            private String id;
            @JsonProperty("contract_id")
            private String contractId;
            @JsonProperty("token_series_id")
            private String tokenSeriesId;
            @JsonProperty("creator_id")
            private String creatorId;
            @JsonProperty("in_circulation")
            private Integer inCirculation;
            @JsonProperty("metadata")
            private MetadataDTO metadata;
            @JsonProperty("price")
            private Object price;
            @JsonProperty("royalty")
            private RoyaltyDTO royalty;
            @JsonProperty("collection")
            private Object collection;
            @JsonProperty("lowest_price")
            private Object lowestPrice;
            @JsonProperty("isMediaCdn")
            private Boolean isMediaCdn;
            @JsonProperty("updated_at")
            private Long updatedAt;
            @JsonProperty("has_price")
            private Object hasPrice;
            @JsonProperty("is_creator")
            private Boolean isCreator;
            @JsonProperty("categories")
            private List<?> categories;
            @JsonProperty("view")
            private Integer view;

            @NoArgsConstructor
            @Data
            public static class MetadataDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("symbol")
                private String symbol;
                @JsonProperty("description")
                private String description;
                @JsonProperty("seller_fee_basis_points")
                private Integer sellerFeeBasisPoints;
                @JsonProperty("image")
                private String image;
                @JsonProperty("external_url")
                private String externalUrl;
                @JsonProperty("attributes")
                private List<AttributesDTO> attributes;
                @JsonProperty("properties")
                private PropertiesDTO properties;
                @JsonProperty("title")
                private String title;
                @JsonProperty("media")
                private String media;
                @JsonProperty("media_hash")
                private Object mediaHash;
                @JsonProperty("issued_at")
                private String issuedAt;
                @JsonProperty("expires_at")
                private Object expiresAt;
                @JsonProperty("starts_at")
                private Object startsAt;
                @JsonProperty("updated_at")
                private Object updatedAt;
                @JsonProperty("extra")
                private Object extra;
                @JsonProperty("reference")
                private String reference;
                @JsonProperty("reference_hash")
                private Object referenceHash;
                @JsonProperty("creator_id")
                private String creatorId;
                @JsonProperty("collection")
                private Object collection;

                @NoArgsConstructor
                @Data
                public static class PropertiesDTO {
                    @JsonProperty("files")
                    private List<FilesDTO> files;
                    @JsonProperty("category")
                    private String category;
                    @JsonProperty("creators")
                    private List<CreatorsDTO> creators;

                    @NoArgsConstructor
                    @Data
                    public static class FilesDTO {
                        @JsonProperty("uri")
                        private String uri;
                        @JsonProperty("type")
                        private String type;
                    }

                    @NoArgsConstructor
                    @Data
                    public static class CreatorsDTO {
                        @JsonProperty("address")
                        private String address;
                        @JsonProperty("share")
                        private Integer share;
                    }
                }

                @NoArgsConstructor
                @Data
                public static class AttributesDTO {
                    @JsonProperty("trait_type")
                    private String traitType;
                    @JsonProperty("value")
                    private String value;
                }
            }

            @NoArgsConstructor
            @Data
            public static class RoyaltyDTO {
                @JsonProperty("asac.near")
                private String _$AsacNear0;// FIXME check this code
            }
        }
    }
}
