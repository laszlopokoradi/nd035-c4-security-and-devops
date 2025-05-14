package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTests {

    private final TestEntityManager entityManager;
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(TestEntityManager entityManager, UserRepository userRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    private User user;

    @BeforeEach
    void setUp() {
        // Create a test user
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");

        // Create a test cart for the user
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotal(new BigDecimal("0.00"));
        cart.setItems(new ArrayList<>());

        // Link user to cart
        user.setCart(cart);

        // Save entities to an in-memory database
        entityManager.persist(cart);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByIdShouldReturnUser() {
        // When
        Optional<User> foundUser = userRepository.findById(user.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    void findByUsernameShouldReturnUser() {
        // When
        User foundUser = userRepository.findByUsername("testUser");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
    }

    @Test
    void findByUsernameWithNonExistentUsername_ShouldReturnNull() {
        // When
        User foundUser = userRepository.findByUsername("nonExistentUser");

        // Then
        assertThat(foundUser).isNull();
    }

    @Test
    void saveShouldPersistUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        Cart newCart = new Cart();
        newCart.setUser(newUser);
        newCart.setTotal(new BigDecimal("0.00"));
        newCart.setItems(new ArrayList<>());
        newUser.setCart(newCart);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("newPassword", savedUser.getPassword());
        assertNotNull(savedUser.getCart());
    }

    @Test
    void deleteShouldRemoveUser() {
        // Given
        Long userId = user.getId();

        // When
        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void updateUserShouldPersistChanges() {
        // Given
        user.setUsername("updatedUsername");
        userRepository.save(user);

        // When
        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        // Then
        assertThat(updatedUser).isNotNull();
        assertEquals("updatedUsername", updatedUser.getUsername());
    }

    @Test
    void userShouldHaveAssociatedCart() {
        // When
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getCart()).isNotNull();
        assertEquals(user.getCart().getId(), foundUser.getCart().getId());
    }
}
