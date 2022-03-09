package com.happyzombie.springinitializr.common.bean;

import lombok.Data;

/**
 * @author hbz
 */
@Data
public class Result<T> {

    private static final String SUCCESS_CODE = "0000";

    private static final String ERROR_CODE = "9999";

    private String returnCode;

    private String returnMessage;

    private T data;

    public static Result<Object> successResult(){
        Result<Object> result = new Result<>();
        result.setReturnCode(SUCCESS_CODE);
        result.setReturnMessage("success");
        return result;
    }

    public static <T> Result<T> successResult(T data){
        Result<T> result = new Result<>();
        result.setReturnCode(SUCCESS_CODE);
        result.setReturnMessage("success");
        result.setData(data);
        return result;
    }

    public static Result<String> errorResult(){
        Result<String> result = new Result<>();
        result.setReturnCode(ERROR_CODE);
        result.setReturnMessage("500 error");
        return result;
    }

    public static <T> Result<T> errorResult(T t){
        Result<T> result = new Result<>();
        result.setReturnCode(ERROR_CODE);
        result.setReturnMessage(t.toString());
        return result;
    }

}