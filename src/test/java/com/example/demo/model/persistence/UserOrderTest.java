package com.example.demo.model.persistence;


import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;


class UserOrderTest {

    private UserOrder userOrder;
    private User user;
    private List<Item> items;
    private Cart cart;

    @BeforeEach
    void setUp() {
        userOrder = new UserOrder();
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(new BigDecimal("10.00"));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(new BigDecimal("20.00"));

        items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("30.00"));

        user.setCart(cart);
    }

    @Test
    void testGetAndSetId() {
        // Given
        Long id = 5L;

        // When
        userOrder.setId(id);

        // Then
        assertThat(userOrder.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetUser() {
        // When
        userOrder.setUser(user);

        // Then
        assertThat(userOrder.getUser()).isSameAs(user);
    }

    @Test
    void testGetAndSetItems() {
        // When
        userOrder.setItems(items);

        // Then
        assertThat(userOrder.getItems()).isSameAs(items);
    }

    @Test
    void testGetAndSetTotal() {
        // Given
        BigDecimal total = new BigDecimal("30.00");

        // When
        userOrder.setTotal(total);

        // Then
        assertThat(userOrder.getTotal()).isEqualByComparingTo(total);
    }

    @Test
    void testCreateFromCart() {
        // When
        UserOrder createdOrder = UserOrder.createFromCart(cart);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getUser()).isSameAs(user);
        assertThat(createdOrder.getTotal()).isEqualByComparingTo(cart.getTotal());

        // Items should be a new list with the same contents
        assertThat(createdOrder.getItems()).isNotSameAs(cart.getItems());
        assertThat(createdOrder.getItems()).hasSize(2);
        assertThat(createdOrder.getItems()).containsExactlyElementsOf(cart.getItems());
    }

    @Test
    void testCreateFromCartWithEmptyItemsList() {
        // Given
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        // When
        UserOrder createdOrder = UserOrder.createFromCart(cart);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getItems()).isEmpty();
        assertThat(createdOrder.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testCreateFromCartWithNullProperties() {
        // Given
        Cart emptyCart = new Cart();
        // Don't set any properties

        // When
        Throwable thrown = catchThrowable(() -> {
            com.example.demo.model.persistence.UserOrder.createFromCart(emptyCart);
        });

        // Then
        assertThat(thrown).isInstanceOf(NullPointerException.class)
                          .hasMessageContaining("Cannot invoke \"java.util.Collection.toArray()\" because \"c\" is null");
    }

    @Test
    void testUserOrderWithNullProperties() {
        // Assert default state
        assertThat(userOrder.getId()).isNull();
        assertThat(userOrder.getItems()).isNull();
        assertThat(userOrder.getUser()).isNull();
        assertThat(userOrder.getTotal()).isNull();
    }

    @Test
    void testUserOrderWithEmptyItemsList() {
        // Given
        List<Item> emptyItems = new ArrayList<>();

        // When
        userOrder.setItems(emptyItems);

        // Then
        assertThat(userOrder.getItems()).isEmpty();
    }
}
