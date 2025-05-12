package com.example.demo;


import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;
import org.springframework.web.client.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTests {
    RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/item/name/Item1", "/api/item/1", "/api/item", "/api/order/history/username", "/api/user/id/1", "/api/user/username"})
    void testUnauthorizedAccessGet(String path) {
        final String baseUrl = "http://localhost:" + port;
        Throwable thrown = catchThrowable(() -> {
            restTemplate.getForObject(baseUrl + path, String.class);
        });
        assertThat(thrown)
                .isInstanceOf(HttpClientErrorException.Forbidden.class)
                .hasMessageContaining("403");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/cart/addToCart", "/api/cart/removeFromCart", "/api/order/submit/username"})
    void testUnauthorizedAccessPost(String path) {
        final String baseUrl = "http://localhost:" + port;
        Throwable thrown = catchThrowable(() -> {
            restTemplate.postForObject(baseUrl + path, null, String.class);
        });
        assertThat(thrown)
                .isInstanceOf(HttpClientErrorException.Forbidden.class)
                .hasMessageContaining("403");
    }
}
