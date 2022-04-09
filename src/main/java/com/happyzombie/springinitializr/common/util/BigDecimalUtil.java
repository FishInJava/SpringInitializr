package com.happyzombie.springinitializr.common.util;

import java.math.BigDecimal;

/**
 * @author admin
 */
public class BigDecimalUtil {
    /**
     * N个BigDecimal相加，忽略为null的项
     */
    public static BigDecimal safePlus(BigDecimal... bigDecimals) {
        BigDecimal retDecimal = BigDecimal.ZERO;
        if (bigDecimals == null) {
            return retDecimal;
        }
        for (BigDecimal i : bigDecimals) {
            if (i == null) {
                continue;
            }
            retDecimal = retDecimal.add(i);
        }
        return retDecimal;
    }
}
