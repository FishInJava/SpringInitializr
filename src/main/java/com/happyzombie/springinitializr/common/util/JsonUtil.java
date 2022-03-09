package com.happyzombie.springinitializr.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class JsonUtil {

    private static class JsonUtilException extends RuntimeException {
        public JsonUtilException() {
            super("JsonUtil Error");
        }
    }

    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 对于空的对象转json的时候不抛出错误
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 允许属性名称没有引号
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置输出时包含属性的风格
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> T jsonStringToObject(String jsonStr, Class<T> clazz) {
        try {
            return MAPPER.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.error("JsonUtil jsonStringToObject error", e);
            throw new JsonUtilException();
        }
    }

    public static String objectToString(Object object){
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("JsonUtil objectToString error", e);
            throw new JsonUtilException();
        }
    }

    public static Map beanToMap(Object object) {
        try {
            return MAPPER.readValue(objectToString(object), Map.class);
        } catch (Exception e) {
            log.error("JsonUtil beanToMap error", e);
            throw new JsonUtilException();
        }
    }


}
