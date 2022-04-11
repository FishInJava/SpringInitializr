package com.happyzombie.springinitializr;


//import org.apache.commons.codec.binary.Base64;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

//@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MainTest {

    @Test
    public void baseCollect() {
        final byte[] bytes = {34, 48, 34};
        final String s = new String(bytes);

    }

    public String decryptByBase64(String base64, String filePath) {
        if (base64 == null && filePath == null) {
            return "生成文件失败，请给出相应的数据。";
        }
        try {
            Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "指定路径下生成文件成功！";
    }

    @Test
    public void read() throws Exception {
        String fileName = "D:\\Code\\near-chain\\near-analyze\\src\\test\\java\\resources\\a";
        // java 11 开始提供的方法，读取文件不能超过2G，与你的内存息息相关
        String s = Files.readString(Paths.get(fileName));
        final String s1 = decryptByBase64(s, "D:\\Code\\near-chain\\near-analyze\\src\\test\\java\\resources\\contract.wasm");
//        final byte[] decode1 = BaseEncoding.base64().decode(s);
//        final String s2 = new String(decode1, StandardCharsets.UTF_8);

//        final Base64 base64 = new Base64(true);
//        String a = "U1RBVEU=";
//        final String s1 = new String(base64.decode(a.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        final byte[] decode = Base64.getMimeDecoder().decode(s.getBytes(StandardCharsets.UTF_8));
        final String s2 = new String(decode, StandardCharsets.UTF_8);
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
