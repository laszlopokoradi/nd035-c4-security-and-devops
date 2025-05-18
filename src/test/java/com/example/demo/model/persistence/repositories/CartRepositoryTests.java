package com.example.demo.model.persistence.repositories;


import static org.assertj.core.api.Assertions.*;

import com.example.demo.model.persistence.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;


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
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        cart = new Cart();
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);
        cart.setItems(new ArrayList<>());

        user.setCart(cart);

        item = new Item();
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(19.99));
        item.setDescription("Test item description");

        entityManager.persist(item);
        entityManager.persist(cart);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByUserShouldReturnCart() {
        Cart foundCart = cartRepository.findByUser(user);

        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getId()).isEqualTo(cart.getId());
        assertThat(foundCart.getUser()).isEqualTo(user);
    }

    @Test
    void findByUserWithNonExistentUserShouldReturnNull() {
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");
        nonExistentUser.setPassword("password");

        Cart foundCart = cartRepository.findByUser(nonExistentUser);

        assertThat(foundCart).isNull();
    }

    @Test
    void saveShouldPersistCart() {
        Cart newCart = new Cart();
        newCart.setTotal(BigDecimal.valueOf(29.99));

        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        newUser.setCart(newCart);

        newCart.setUser(newUser);
        newCart.setItems(new ArrayList<>());

        entityManager.persist(newUser);

        Cart savedCart = cartRepository.save(newCart);

        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(29.99));
        assertThat(savedCart.getUser()).isNotNull();
        assertThat(savedCart.getUser().getUsername()).isEqualTo("newUser");
    }

    @Test
    void findByIdShouldReturnCart() {
        Optional<Cart> foundCart = cartRepository.findById(cart.getId());

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo(cart.getId());
    }

    @Test
    void deleteShouldRemoveCart() {
        Long cartId = cart.getId();

        cartRepository.delete(cart);
        Optional<Cart> deletedCart = cartRepository.findById(cartId);

        assertThat(deletedCart).isNotPresent();
    }

    @Test
    void addItemToCartShouldUpdateCartTotal() {
        cart.addItem(item);
        cartRepository.save(cart);

        Cart updatedCart = cartRepository.findById(cart.getId())
                                         .orElse(null);

        assertThat(updatedCart).isNotNull();
        assertThat(updatedCart.getItems()).hasSize(1);
        assertThat(updatedCart.getTotal()).isEqualByComparingTo(item.getPrice());
    }

    @Test
    void removeItemFromCartShouldUpdateCartTotal() {
        cart.addItem(item);
        cartRepository.save(cart);

        cart.removeItem(item);
        cartRepository.save(cart);
        Cart updatedCart = cartRepository.findById(cart.getId())
                                         .orElse(null);

        assertThat(updatedCart).isNotNull();
        assertThat(updatedCart.getItems()).isEmpty();
        assertThat(updatedCart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
