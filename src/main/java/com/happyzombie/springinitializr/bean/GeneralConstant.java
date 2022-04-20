package com.happyzombie.springinitializr.bean;

import java.math.BigDecimal;

/**
 * @author admin
 */
public interface GeneralConstant {
    /**
     * function_call动作中的内部方法
     */
    String FUNCTION_CALL_ADD_REQUEST_AND_CONFIRM = "add_request_and_confirm";
    String FUNCTION_CALL_CONFIRM = "confirm";
    String FUNCTION_CALL_DELETE_REQUEST = "delete_request";

    /**
     * Near精度
     */
    BigDecimal NEAR_PRECISION = new BigDecimal("1000000000000000000000000");
}
