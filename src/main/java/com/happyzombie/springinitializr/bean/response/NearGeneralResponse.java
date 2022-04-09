package com.happyzombie.springinitializr.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@NoArgsConstructor
@Data
public class NearGeneralResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("error")
    private Object error;
}
