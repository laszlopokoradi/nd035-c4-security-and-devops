package com.example.demo.model.persistence;


import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;


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
        long id = 10L;

        User returnedUser = user.setId(id);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetUsername() {
        String username = "testUser";

        User returnedUser = user.setUsername(username);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetPassword() {
        String password = "password123";

        User returnedUser = user.setPassword(password);

        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testGetAndSetCart() {
        User returnedUser = user.setCart(cart);

        assertThat(user.getCart()).isSameAs(cart);
        assertThat(returnedUser).isSameAs(user); // Verify method chaining returns this
    }

    @Test
    void testUserHasNullCartByDefault() {
        assertThat(user.getCart()).isNull();
    }

    @Test
    void testUserWithNullUsername() {
        user.setUsername(null);

        assertThat(user.getUsername()).isNull();
    }

    @Test
    void testUserWithNullPassword() {
        user.setPassword(null);

        assertThat(user.getPassword()).isNull();
    }

    @Test
    void testUserWithEmptyUsername() {
        String emptyUsername = "";

        user.setUsername(emptyUsername);

        assertThat(user.getUsername()).isEmpty();
    }

    @Test
    void testUserWithEmptyPassword() {
        String emptyPassword = "";

        user.setPassword(emptyPassword);

        assertThat(user.getPassword()).isEmpty();
    }
}
