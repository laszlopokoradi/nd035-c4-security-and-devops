package com.example.demo.controllers;


import com.example.demo.*;
import com.example.demo.model.persistence.*;
import com.example.demo.model.requests.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;
import com.splunk.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = EcommerceApplication.class)
@Transactional
class UserControllerTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTests.class);
    private final UserController userController;

    @Autowired
    public UserControllerTests(UserController userController) {
        this.userController = userController;
    }

    @Test
    void contextLoads() {
        LOGGER.debug("[DEBUG_LOG] Testing debug log level");
        LOGGER.info("[INFO_LOG] Testing info log level");
        LOGGER.warn("[WARN_LOG] Testing warn log level");
        LOGGER.error("[ERROR_LOG] Testing error log level");
    }

    @Test
    void testCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setRepeatedPassword("testPassword");

        ResponseEntity<User> createdUser = userController.createUser(createUserRequest);

        assertThat(createdUser.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdUser.getBody()).isNotNull();
        assertThat(createdUser.getBody()
                              .getId()).isNotZero();
        assertThat(createdUser.getBody()
                              .getUsername()).isEqualTo("testUser");
    }

    @Test
    void testCreateUserFailOnIncorrectRepeatedPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setRepeatedPassword("wrongPassword");

        ResponseEntity<User> createdUser = userController.createUser(createUserRequest);

        assertThat(createdUser.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateUserFailOnWeakPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("weak");
        createUserRequest.setRepeatedPassword("weak");

        ResponseEntity<User> createdUser = userController.createUser(createUserRequest);

        assertThat(createdUser.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testCreateUserFailOnExistingUsername() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setRepeatedPassword("testPassword");

        ResponseEntity<User> createdUser1 = userController.createUser(createUserRequest);
        assertThat(createdUser1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<User> createdUser2 = userController.createUser(createUserRequest);
        assertThat(createdUser2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testGetUserById() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setRepeatedPassword("testPassword");

        ResponseEntity<User> createdUser = userController.createUser(createUserRequest);
        assertThat(createdUser.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdUser.getBody()).isNotNull();

        Long userId = createdUser.getBody()
                                 .getId();

        ResponseEntity<User> retrievedUser = userController.findById(userId);
        assertThat(retrievedUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(retrievedUser.getBody()).isNotNull();
        assertThat(retrievedUser.getBody()
                                .getUsername()).isEqualTo("testUser");
    }
}
