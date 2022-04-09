package com.happyzombie.springinitializr;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MainTest {

    @Test
    public void baseCollect() {
        final byte[] bytes = {34, 48, 34};
        final String s = new String(bytes);
    }

    @Test
    public void read() {
        //
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("account_id", "woshishabi.near");
        final String s1 = request.toString();
        final String s = Base64.getEncoder().encodeToString(s1.getBytes(StandardCharsets.UTF_8));
        System.out.println(1);
    }

    @Test
    public void sum() {
        final BigDecimal bigDecimal = new BigDecimal("10454540.325945396");
        final BigDecimal bigInteger = new BigDecimal("4562243140441355087716496");
        final BigDecimal bigInteger2 = new BigDecimal("44229803183229136864810306290");

        final BigDecimal multiply = bigInteger.multiply(bigDecimal.divide(bigInteger2, 4, RoundingMode.HALF_UP));


        final BigDecimal divide = bigInteger.multiply(bigDecimal).divide(bigInteger2, 4, RoundingMode.HALF_UP);
        System.out.println();
    }
}
