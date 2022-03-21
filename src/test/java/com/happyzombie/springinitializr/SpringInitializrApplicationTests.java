package com.happyzombie.springinitializr;

import com.happyzombie.springinitializr.service.NearExplorerBackendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringInitializrApplicationTests {

    @Autowired
    NearExplorerBackendService nearExplorerBackendService;

    @Test
    void contextLoads() {
        nearExplorerBackendService.getTransactionsListByAccountId("witt.near", 1646907653388L, 0);
    }

}
