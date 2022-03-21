package com.happyzombie.springinitializr;

import com.happyzombie.springinitializr.api.NearExplorerBackendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;

@SpringBootTest
class SpringInitializrApplicationTests {

    @Autowired
    NearExplorerBackendService nearExplorerBackendService;

    @Test
    void contextLoads() {
        final BigInteger bigInteger = new BigInteger("1646907653388");
        nearExplorerBackendService.getTransactionsListByAccountId("witt.near", bigInteger, 0);
    }

}
