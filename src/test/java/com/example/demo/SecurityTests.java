package com.example.demo;


import com.example.demo.model.persistence.*;
import com.example.demo.model.requests.*;
import kong.unirest.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTests {
    public static final String HOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testCreateUser() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response = createUser(baseUrl, "test1", "test1Password");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).hasFieldOrProperty("id");
        assertThat(response.getBody()).hasFieldOrProperty("username");
    }

    @Test
    void testCreateUserFailOnIncorrectRepeatedPassword() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response = createUser(baseUrl, "test2", "test1Password", "test2Password");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateUserFailOnWeakPassword() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response = createUser(baseUrl, "test3",
                "test3");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateUserFailOnExistingUsername() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response1 = createUser(baseUrl, "test4",
                "test4Password");

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.CREATED);

        HttpResponse<User> response2 = createUser(baseUrl, "test4",
                "test4Password");

        assertThat(response2.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/item/name/Item1", "/api/item/1", "/api/item", "/api/order/history", "/api/user/id/1",
                            "/api/user/username"})
    void testUnauthorizedAccessGet(String path) {
        final String baseUrl = HOST + port + path;

        HttpResponse<String> response = Unirest.get(baseUrl)
                                               .header("accept", "*/*")
                                               .asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/cart/addToCart", "/api/cart/removeFromCart", "/api/order/submit"})
    void testUnauthorizedAccessPost(String path) {
        final String baseUrl = HOST + port + path;

        HttpResponse<String> response = Unirest.post(baseUrl)
                                               .header("accept", "*/*")
                                               .contentType(ContentType.APPLICATION_JSON)
                                               .body("{\"itemId\": 1}")
                                               .asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testLogin() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response1 = createUser(
                baseUrl, "test5", "test5Password");

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.CREATED);

        HttpResponse<String> response2 = performLogin(baseUrl, "test5", "test5Password");

        assertThat(response2.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getHeaders()
                            .getFirst("Authorization")).isNotNull();
        assertThat(response2.getHeaders()
                            .getFirst("Authorization")).startsWith("Bearer ");
    }

    @Test
    void testLoginFailOnIncorrectPassword() {
        final String baseUrl = HOST + port;

        HttpResponse<User> response1 = createUser(
                baseUrl, "test6", "test6Password");

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.CREATED);

        HttpResponse<String> response2 = performLogin(baseUrl, "test6", "wrongPassword");

        assertThat(response2.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testLoginFailOnNonExistentUser() {
        final String baseUrl = HOST + port;

        HttpResponse<String> response = performLogin(baseUrl, "nonExistentUser", "password");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Nested
    class AuthorizedAccessTests {
        static String token;

        @BeforeAll
        static void setup(@LocalServerPort int port) {
            final String baseUrl = HOST + port;

            HttpResponse<User> response = createUser(baseUrl, "test99", "test99Password");

            assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);

            HttpResponse<String> loginResponse = performLogin(baseUrl, "test99", "test99Password");

            assertThat(loginResponse.getStatus()).isEqualTo(HttpStatus.OK);

            token = loginResponse.getHeaders()
                                 .getFirst("Authorization");
        }


        @ParameterizedTest
        @ValueSource(strings = {"/api/item/name/Item1", "/api/item/1", "/api/item", "/api/order/history", "/api/user/id/1",
                                "/api/user/username"})
        void testAuthorizedAccessGet(String path) {
            final String baseUrl = HOST + port + path;

            HttpResponse<String> response = Unirest.get(baseUrl)
                                                   .header("Authorization", token)
                                                   .header("accept", "*/*")
                                                   .asString();

            assertThat(response.getStatus()).isNotEqualTo(HttpStatus.FORBIDDEN);
        }

        @ParameterizedTest
        @ValueSource(strings = {"/api/cart/addToCart", "/api/cart/removeFromCart", "/api/order/submit"})
        void testAuthorizedAccessPost(String path) {
            final String baseUrl = HOST + port + path;

            HttpResponse<String> response = Unirest.post(baseUrl)
                                                   .header("Authorization", token)
                                                   .header("accept", "*/*")
                                                   .contentType(ContentType.APPLICATION_JSON)
                                                   .body("{\"itemId\": 1}")
                                                   .asString();

            assertThat(response.getStatus()).isNotEqualTo(HttpStatus.FORBIDDEN);
        }
    }


    private static HttpResponse<String> performLogin(String baseUrl, String username, String password) {
        String loginBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        return Unirest.post(baseUrl + "/login")
                      .header("accept", "*/*")
                      .contentType(ContentType.APPLICATION_JSON)
                      .body(loginBody)
                      .asString();
    }

    private static HttpResponse<User> createUser(String baseUrl, String user, String password) {
        return createUser(baseUrl, user, password, password);
    }

    private static HttpResponse<User> createUser(String baseUrl, String user, String password, String repeatedPassword) {
        return Unirest.post(baseUrl + "/api/user/create")
                      .header("accept", "*/*")
                      .contentType(ContentType.APPLICATION_JSON)
                      .body(new CreateUserRequest().setUsername(user)
                                                   .setPassword(password)
                                                   .setRepeatedPassword(repeatedPassword))
                      .asObject(User.class);
    }
}
