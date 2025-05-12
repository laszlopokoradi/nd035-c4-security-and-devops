package com.example.demo;


import static org.assertj.core.api.Assertions.*;

import com.example.demo.model.persistence.*;
import com.example.demo.model.requests.*;
import kong.unirest.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTests {

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testCreateUser() {
        final String baseUrl = "http://localhost:" + port;

        HttpResponse<User> response = createUser(baseUrl, "test1", "test1Password");

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getBody()).hasFieldOrProperty("id");
        assertThat(response.getBody()).hasFieldOrProperty("username");
    }

    @Test
    void testCreateUserFailOnIncorrectRepeatedPassword() {
        final String baseUrl = "http://localhost:" + port;

        HttpResponse<User> response = createUser(baseUrl, "test2", "test1Password", "test2Password");

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void testCreateUserFailOnWeakPassword() {
        final String baseUrl = "http://localhost:" + port;

        HttpResponse<User> response = createUser(baseUrl, "test3",
                "test3");

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void testCreateUserFailOnExistingUsername() {
        final String baseUrl = "http://localhost:" + port;

        HttpResponse<User> response1 = createUser(baseUrl, "test4",
                "test4Password");

        assertThat(response1.getStatus()).isEqualTo(201);

        HttpResponse<User> response2 = createUser(baseUrl, "test4",
                "test4Password");

        assertThat(response2.getStatus()).isEqualTo(409);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/item/name/Item1", "/api/item/1", "/api/item", "/api/order/history", "/api/user/id/1",
            "/api/user/username"})
    void testUnauthorizedAccessGet(String path) {
        final String baseUrl = "http://localhost:" + port + path;

        HttpResponse<String> response = Unirest.get(baseUrl)
                                               .header("accept", "*/*")
                                               .asString();

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/cart/addToCart", "/api/cart/removeFromCart", "/api/order/submit"})
    void testUnauthorizedAccessPost(String path) {
        final String baseUrl = "http://localhost:" + port + path;

        HttpResponse<String> response = Unirest.post(baseUrl)
                                               .header("accept", "*/*")
                                               .asString();

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void testLogin() {
        final String baseUrl = "http://localhost:" + port;

        HttpResponse<User> response1 = createUser(
                baseUrl, "test5", "test5Password");

        assertThat(response1.getStatus()).isEqualTo(201);

        HttpResponse<String> response2 = Unirest.post(baseUrl + "/login")
                                                .header("accept", "*/*")
                                                .contentType(ContentType.APPLICATION_JSON)
                                                .body("{\"username\": \"test5\", \"password\": \"test5Password\"}")
                                                .asString();

        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getHeaders().getFirst("Authorization")).isNotNull();
        assertThat(response2.getHeaders().getFirst("Authorization")).startsWith("Bearer ");
    }

    private HttpResponse<User> createUser(String baseUrl, String user, String password) {
        return createUser(baseUrl, user, password, password);
    }

    private HttpResponse<User> createUser(String baseUrl, String user, String password, String repeatedPassword ) {
        return Unirest.post(baseUrl + "/api/user/create")
                      .header("accept", "*/*")
                      .contentType(ContentType.APPLICATION_JSON)
                      .body(new CreateUserRequest().setUsername(user)
                                                   .setPassword(password)
                                                   .setRepeatedPassword(repeatedPassword))
                      .asObject(User.class);
    }
}
