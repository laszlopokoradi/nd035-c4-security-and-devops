package com.example.demo.model.persistence;


import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;


class UserTest {

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        cart = new Cart();
    }

    @Test
    void testGetAndSetId() {
        // Given
        long id = 10L;

        // When
        User returnedUser = user.setId(id);

        // Then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetUsername() {
        // Given
        String username = "testUser";

        // When
        User returnedUser = user.setUsername(username);

        // Then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetPassword() {
        // Given
        String password = "password123";

        // When
        User returnedUser = user.setPassword(password);

        // Then
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetCart() {
        // When
        User returnedUser = user.setCart(cart);

        // Then
        assertThat(user.getCart()).isSameAs(cart);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testUserHasNullCartByDefault() {
        // Then
        assertThat(user.getCart()).isNull();
    }

    @Test
    void testUserWithNullUsername() {
        // Given
        user.setUsername(null);

        // Then
        assertThat(user.getUsername()).isNull();
    }

    @Test
    void testUserWithNullPassword() {
        // Given
        user.setPassword(null);

        // Then
        assertThat(user.getPassword()).isNull();
    }

    @Test
    void testUserWithEmptyUsername() {
        // Given
        String emptyUsername = "";

        // When
        user.setUsername(emptyUsername);

        // Then
        assertThat(user.getUsername()).isEmpty();
    }

    @Test
    void testUserWithEmptyPassword() {
        // Given
        String emptyPassword = "";

        // When
        user.setPassword(emptyPassword);

        // Then
        assertThat(user.getPassword()).isEmpty();
    }
}
