package com.example.demo.model.persistence;


import static org.assertj.core.api.Assertions.*;

import java.math.*;
import org.junit.jupiter.api.*;


class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(BigDecimal.valueOf(19.99));
    }

    @Test
    void testGetAndSetId() {
        Long id = 5L;

        item.setId(id);

        assertThat(item.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetName() {
        String name = "New Item Name";

        item.setName(name);

        assertThat(item.getName()).isEqualTo(name);
    }

    @Test
    void testGetAndSetDescription() {
        String description = "New Item Description";

        item.setDescription(description);

        assertThat(item.getDescription()).isEqualTo(description);
    }

    @Test
    void testGetAndSetPrice() {
        BigDecimal price = BigDecimal.valueOf(29.99);

        item.setPrice(price);

        assertThat(item.getPrice()).isEqualByComparingTo(price);
    }

    @Test
    void testEqualsWithSameObject() {
        assertThat(item).isEqualTo(item);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Object other = new Object();

        assertThat(item).isNotEqualTo(other);
    }

    @Test
    void testEqualsWithSameId() {
        Item otherItem = new Item();
        otherItem.setId(1L);
        otherItem.setName("Different Name");
        otherItem.setDescription("Different Description");
        otherItem.setPrice(BigDecimal.valueOf(99.99));

        assertThat(item).isEqualTo(otherItem);
    }

    @Test
    void testEqualsWithDifferentId() {
        Item otherItem = new Item();
        otherItem.setId(2L);
        otherItem.setName(item.getName());
        otherItem.setDescription(item.getDescription());
        otherItem.setPrice(item.getPrice());

        assertThat(item).isNotEqualTo(otherItem);
    }

    @Test
    void testEqualsWithBothNullIds() {
        Item item1 = new Item();
        Item item2 = new Item();

        item1.setId(null);
        item2.setId(null);

        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void testEqualsWithOneNullId() {
        Item item1 = new Item();
        Item item2 = new Item();

        item1.setId(null);
        item2.setId(1L);

        assertThat(item1).isNotEqualTo(item2);
        assertThat(item2).isNotEqualTo(item1);
    }

    @Test
    void testHashCodeWithSameId() {
        Item otherItem = new Item();
        otherItem.setId(1L);

        assertThat(item).hasSameHashCodeAs(otherItem);
    }

    @Test
    void testHashCodeWithDifferentId() {
        Item otherItem = new Item();
        otherItem.setId(2L);

        assertThat(item.hashCode()).isNotEqualTo(otherItem.hashCode());
    }

    @Test
    void testHashCodeWithNullId() {
        Item item1 = new Item();
        Item item2 = new Item();

        item1.setId(null);
        item2.setId(null);

        assertThat(item1).hasSameHashCodeAs(item2);
    }
}
