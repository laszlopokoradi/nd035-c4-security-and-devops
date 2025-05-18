package com.example.demo.model.persistence;


import static org.assertj.core.api.Assertions.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;


class CartTest {

    private Cart cart;
    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        user = new User();

        item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(BigDecimal.valueOf(10.00));

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(BigDecimal.valueOf(20.00));
    }

    @Test
    void testGetAndSetId() {
        Long id = 5L;

        cart.setId(id);

        assertThat(cart.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetUser() {
        cart.setUser(user);

        assertThat(cart.getUser()).isSameAs(user);
    }

    @Test
    void testGetAndSetTotal() {
        BigDecimal total = BigDecimal.valueOf(30.00);

        cart.setTotal(total);

        assertThat(cart.getTotal()).isEqualByComparingTo(total);
    }

    @Test
    void testGetAndSetItems() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        cart.setItems(items);

        assertThat(cart.getItems()).containsExactly(item1, item2);
    }

    @Test
    void testAddItemToNullItemsList() {
        cart.setItems(null);
        cart.setTotal(null);

        cart.addItem(item1);

        assertThat(cart.getItems()).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()
                       .getFirst()).isSameAs(item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(item1.getPrice());
    }

    @Test
    void testAddItemToEmptyItemsList() {
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        cart.addItem(item1);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()
                       .getFirst()).isSameAs(item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(item1.getPrice());
    }

    @Test
    void testAddMultipleItems() {
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item1); // Add duplicate item

        assertThat(cart.getItems()).hasSize(3);
        assertThat(cart.getItems()).containsExactly(item1, item2, item1);

        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
    }

    @Test
    void testRemoveItemFromNullItemsList() {
        cart.setItems(null);
        cart.setTotal(null);

        cart.removeItem(item1);

        assertThat(cart.getItems()).isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testRemoveItemFromEmptyItemsList() {
        cart.setItems(new ArrayList<>());
        cart.setTotal(null);

        cart.removeItem(item1);

        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testRemoveExistingItem() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(30.00));

        cart.removeItem(item1);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()).containsExactly(item2);
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    }

    @Test
    void testRemoveNonExistingItem() {
        List<Item> items = new ArrayList<>();
        items.add(item2);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(20.00));

        Item nonExistingItem = new Item();
        nonExistingItem.setId(3L);
        nonExistingItem.setPrice(BigDecimal.valueOf(15.00));

        cart.removeItem(nonExistingItem);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()).containsExactly(item2);
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    }

    @Test
    void testRemoveItemWhenMultipleExist() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item1); // Add duplicate
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(40.00));

        cart.removeItem(item1);

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getItems()).containsExactly(item2, item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }
}
