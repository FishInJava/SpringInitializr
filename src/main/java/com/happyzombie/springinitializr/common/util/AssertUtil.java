package com.happyzombie.springinitializr.common.util;

import org.springframework.util.Assert;

public class AssertUtil {

    public static void shouldBeTrue(boolean expression, String message) {
        Assert.isTrue(expression, message);
    }

}
