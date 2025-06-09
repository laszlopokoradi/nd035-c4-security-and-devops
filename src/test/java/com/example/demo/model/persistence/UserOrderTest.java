package com.example.demo.model.persistence;


import static org.assertj.core.api.Assertions.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;


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
        item1.setPrice(BigDecimal.valueOf(10.00));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(BigDecimal.valueOf(20.00));

        items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(30.00));

        user.setCart(cart);
    }

    @Test
    void testGetAndSetId() {
        Long id = 5L;

        userOrder.setId(id);

        assertThat(userOrder.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetUser() {
        userOrder.setUser(user);

        assertThat(userOrder.getUser()).isSameAs(user);
    }

    @Test
    void testGetAndSetItems() {
        userOrder.setItems(items);

        assertThat(userOrder.getItems()).isSameAs(items);
    }

    @Test
    void testGetAndSetTotal() {
        BigDecimal total = BigDecimal.valueOf(30.00);

        userOrder.setTotal(total);

        assertThat(userOrder.getTotal()).isEqualByComparingTo(total);
    }

    @Test
    void testCreateFromCart() {
        UserOrder createdOrder = UserOrder.createFromCart(cart);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getUser()).isSameAs(user);
        assertThat(createdOrder.getTotal()).isEqualByComparingTo(cart.getTotal());

        assertThat(createdOrder.getItems()).isNotSameAs(cart.getItems());
        assertThat(createdOrder.getItems()).hasSize(2);
        assertThat(createdOrder.getItems()).containsExactlyElementsOf(cart.getItems());
    }

    @Test
    void testCreateFromCartWithEmptyItemsList() {
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        UserOrder createdOrder = UserOrder.createFromCart(cart);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getItems()).isEmpty();
        assertThat(createdOrder.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testCreateFromCartWithNullProperties() {
        Cart emptyCart = new Cart();

        Throwable thrown = catchThrowable(() -> UserOrder.createFromCart(emptyCart));

        assertThat(thrown).isInstanceOf(NullPointerException.class)
                          .hasMessageContaining(
                                  "Cannot invoke \"java.util.Collection.toArray()\"");
    }

    @Test
    void testUserOrderWithNullProperties() {
        assertThat(userOrder.getId()).isNull();
        assertThat(userOrder.getItems()).isNull();
        assertThat(userOrder.getUser()).isNull();
        assertThat(userOrder.getTotal()).isNull();
    }

    @Test
    void testUserOrderWithEmptyItemsList() {
        List<Item> emptyItems = new ArrayList<>();

        userOrder.setItems(emptyItems);

        assertThat(userOrder.getItems()).isEmpty();
    }
}
