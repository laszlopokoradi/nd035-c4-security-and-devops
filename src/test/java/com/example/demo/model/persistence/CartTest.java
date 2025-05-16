package com.example.demo.model.persistence;


import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;


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
        item1.setPrice(new BigDecimal("10.00"));

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(new BigDecimal("20.00"));
    }

    @Test
    void testGetAndSetId() {
        // Given
        Long id = 5L;

        // When
        cart.setId(id);

        // Then
        assertThat(cart.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetUser() {
        // When
        cart.setUser(user);

        // Then
        assertThat(cart.getUser()).isSameAs(user);
    }

    @Test
    void testGetAndSetTotal() {
        // Given
        BigDecimal total = new BigDecimal("30.00");

        // When
        cart.setTotal(total);

        // Then
        assertThat(cart.getTotal()).isEqualByComparingTo(total);
    }

    @Test
    void testGetAndSetItems() {
        // Given
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // When
        cart.setItems(items);

        // Then
        assertThat(cart.getItems()).containsExactly(item1, item2);
    }

    @Test
    void testAddItemToNullItemsList() {
        // Given
        cart.setItems(null);
        cart.setTotal(null);

        // When
        cart.addItem(item1);

        // Then
        assertThat(cart.getItems()).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()
                       .getFirst()).isSameAs(item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(item1.getPrice());
    }

    @Test
    void testAddItemToEmptyItemsList() {
        // Given
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        // When
        cart.addItem(item1);

        // Then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()
                       .getFirst()).isSameAs(item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(item1.getPrice());
    }

    @Test
    void testAddMultipleItems() {
        // Given
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        // When
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item1); // Add duplicate item

        // Then
        assertThat(cart.getItems()).hasSize(3);
        assertThat(cart.getItems()).containsExactly(item1, item2, item1);

        // Total should be sum of all items: 10 + 20 + 10 = 40
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("40.00"));
    }

    @Test
    void testRemoveItemFromNullItemsList() {
        // Given
        cart.setItems(null);
        cart.setTotal(null);

        // When
        cart.removeItem(item1);

        // Then
        assertThat(cart.getItems()).isNotNull();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testRemoveItemFromEmptyItemsList() {
        // Given
        cart.setItems(new ArrayList<>());
        cart.setTotal(null);

        // When
        cart.removeItem(item1);

        // Then
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testRemoveExistingItem() {
        // Given
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("30.00")); // 10 + 20

        // When
        cart.removeItem(item1);

        // Then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()).containsExactly(item2);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void testRemoveNonExistingItem() {
        // Given
        List<Item> items = new ArrayList<>();
        items.add(item2);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("20.00"));

        Item nonExistingItem = new Item();
        nonExistingItem.setId(3L);
        nonExistingItem.setPrice(new BigDecimal("15.00"));

        // When
        cart.removeItem(nonExistingItem);

        // Then
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems()).containsExactly(item2);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void testRemoveItemWhenMultipleExist() {
        // Given
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item1); // Add duplicate
        cart.setItems(items);
        cart.setTotal(new BigDecimal("40.00")); // 10 + 20 + 10

        // When
        cart.removeItem(item1);

        // Then - Should remove only one occurrence of item1
        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getItems()).containsExactly(item2, item1);
        assertThat(cart.getTotal()).isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
