package com.radomskyi.budgeter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=servlet"
})
class OpenApiConfigTest {

    @Test
    void contextLoads() {
        // This test ensures the OpenAPI configuration loads without errors
        assertTrue(true);
    }
}
