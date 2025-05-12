package com.example.demo.model.persistence.repositories;


import com.example.demo.model.persistence.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class CartRepositoryTests {

    private final TestEntityManager entityManager;
    private final CartRepository cartRepository;

    @Autowired
    public CartRepositoryTests(TestEntityManager entityManager, CartRepository cartRepository) {
        this.entityManager = entityManager;
        this.cartRepository = cartRepository;
    }

    private User user;
    private Cart cart;
    private Item item;

    @BeforeEach
    void setUp() {
        // Create a test user
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        // Create a test cart
        cart = new Cart();
        cart.setUser(user);
        cart.setTotal(new BigDecimal("0.00"));
        cart.setItems(new ArrayList<>());

        // Link user to cart
        user.setCart(cart);

        // Create a test item
        item = new Item();
        item.setName("Test Item");
        item.setPrice(new BigDecimal("19.99"));
        item.setDescription("Test item description");

        // Save entities to an in-memory database
        entityManager.persist(item);
        entityManager.persist(cart);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByUser_ShouldReturnCart() {
        // When
        Cart foundCart = cartRepository.findByUser(user);

        // Then
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getId()).isEqualTo(cart.getId());
        assertThat(foundCart.getUser()).isEqualTo(user);
    }

    @Test
    void findByUser_WithNonExistentUser_ShouldReturnNull() {
        // Given
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");
        nonExistentUser.setPassword("password");

        // When
        Cart foundCart = cartRepository.findByUser(nonExistentUser);

        // Then
        assertThat(foundCart).isNull();
    }

    @Test
    void save_ShouldPersistCart() {
        // Given
        Cart newCart = new Cart();
        newCart.setTotal(new BigDecimal("29.99"));

        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        newUser.setCart(newCart);

        newCart.setUser(newUser);
        newCart.setItems(new ArrayList<>());

        entityManager.persist(newUser);

        // When
        Cart savedCart = cartRepository.save(newCart);

        // Then
        assertNotNull(savedCart.getId());
        assertEquals(new BigDecimal("29.99"), savedCart.getTotal());
        assertEquals("newUser", savedCart.getUser()
                                         .getUsername());
    }

    @Test
    void findById_ShouldReturnCart() {
        // When
        Optional<Cart> foundCart = cartRepository.findById(cart.getId());

        // Then
        assertTrue(foundCart.isPresent());
        assertEquals(cart.getId(), foundCart.get()
                                            .getId());
    }

    @Test
    void delete_ShouldRemoveCart() {
        // Given
        Long cartId = cart.getId();

        // When
        cartRepository.delete(cart);
        Optional<Cart> deletedCart = cartRepository.findById(cartId);

        // Then
        assertFalse(deletedCart.isPresent());
    }

    @Test
    void addItemToCart_ShouldUpdateCartTotal() {
        // Given
        cart.addItem(item);
        cartRepository.save(cart);

        // When
        Cart updatedCart = cartRepository.findById(cart.getId())
                                         .orElse(null);

        // Then
        assertThat(updatedCart).isNotNull();
        assertEquals(1, updatedCart.getItems()
                                   .size());
        assertEquals(item.getPrice(), updatedCart.getTotal());
    }

    @Test
    void removeItemFromCart_ShouldUpdateCartTotal() {
        // Given
        cart.addItem(item);
        cartRepository.save(cart);

        // When
        cart.removeItem(item);
        cartRepository.save(cart);
        Cart updatedCart = cartRepository.findById(cart.getId())
                                         .orElse(null);


        assertThat(updatedCart).isNotNull();
        assertEquals(0, updatedCart.getItems()
                                   .size());
        assertEquals(new BigDecimal("0.00"), updatedCart.getTotal());
    }
}
