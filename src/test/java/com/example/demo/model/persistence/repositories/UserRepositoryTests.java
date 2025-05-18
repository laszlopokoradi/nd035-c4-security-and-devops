package com.example.demo.model.persistence.repositories;

import static org.assertj.core.api.Assertions.*;

import com.example.demo.model.persistence.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

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
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password123");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);
        cart.setItems(new ArrayList<>());

        user.setCart(cart);

        entityManager.persist(cart);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByIdShouldReturnUser() {
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser).get().extracting("id").isEqualTo(user.getId());
        assertThat(foundUser).get().extracting("username").isEqualTo("testUser");
    }

    @Test
    void findByUsernameShouldReturnUser() {
        User foundUser = userRepository.findByUsername("testUser");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
    }

    @Test
    void findByUsernameWithNonExistentUsername_ShouldReturnNull() {
        User foundUser = userRepository.findByUsername("nonExistentUser");

        assertThat(foundUser).isNull();
    }

    @Test
    void saveShouldPersistUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        Cart newCart = new Cart();
        newCart.setUser(newUser);
        newCart.setTotal(BigDecimal.ZERO);
        newCart.setItems(new ArrayList<>());
        newUser.setCart(newCart);

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newUser");
        assertThat(savedUser.getPassword()).isEqualTo("newPassword");
        assertThat(savedUser.getCart()).isNotNull();
    }

    @Test
    void deleteShouldRemoveUser() {
        Long userId = user.getId();

        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findById(userId);

        assertThat(deletedUser).isNotPresent();
    }

    @Test
    void updateUserShouldPersistChanges() {
        user.setUsername("updatedUsername");
        userRepository.save(user);

        User updatedUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getUsername()).isEqualTo("updatedUsername");
    }

    @Test
    void userShouldHaveAssociatedCart() {
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getCart()).isNotNull();
        assertThat(foundUser.getCart().getId()).isEqualTo(user.getCart().getId());
    }
}
