package com.happyzombie.springinitializr.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 访问合约中方法的通用返回值
 *
 * @param <T> 需要getCallFunctionResult返回的结果，通常定义成内部类
 * @author admin
 */
@NoArgsConstructor
@Data
public class NearContractResponse<T> extends NearGeneralResponse {
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    private static class ResultDTO {
        @JsonProperty("block_hash")
        private String blockHash;
        @JsonProperty("block_height")
        private Integer blockHeight;
        @JsonProperty("error")
        private String error;
        @JsonProperty("logs")
        private List<?> logs;
        @JsonProperty("result")
        private byte[] result;
    }

    /**
     * 获取泛型T的class
     */
    @SuppressWarnings("unchecked")
    private Class<T> getClazz() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T getCallFunctionResult() {
        AssertUtil.shouldBeTrue(result != null && getResult().getResult().length > 0, "result is null!!");
        if (getError() != null) {
            throw new RuntimeException(String.format("错误信息不为空，error msg ：%s", getError().toString()));
        }
        final String s = new String(getResult().getResult(), StandardCharsets.UTF_8);
        final Class<T> clazz = getClazz();
        return JsonUtil.jsonStringToObject(s, clazz);
    }
}
