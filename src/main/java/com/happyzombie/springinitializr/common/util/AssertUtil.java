package com.happyzombie.springinitializr.common.util;

import org.springframework.util.Assert;

/**
 * @author admin
 */
public class AssertUtil {

    public static void shouldBeTrue(boolean expression, String message) {
        Assert.isTrue(expression, message);
    }

}
