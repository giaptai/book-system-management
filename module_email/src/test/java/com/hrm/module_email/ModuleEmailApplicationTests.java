package com.hrm.module_email;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class ModuleEmailApplicationTests {

    @Test
    void contextLoads() {
        System.err.println(StandardCharsets.UTF_8.name());
    }

}
