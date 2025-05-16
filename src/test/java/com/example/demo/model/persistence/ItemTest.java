package com.example.demo.model.persistence;


import org.junit.jupiter.api.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;


class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("19.99"));
    }

    @Test
    void testGetAndSetId() {
        // Given
        Long id = 5L;

        // When
        item.setId(id);

        // Then
        assertThat(item.getId()).isEqualTo(id);
    }

    @Test
    void testGetAndSetName() {
        // Given
        String name = "New Item Name";

        // When
        item.setName(name);

        // Then
        assertThat(item.getName()).isEqualTo(name);
    }

    @Test
    void testGetAndSetDescription() {
        // Given
        String description = "New Item Description";

        // When
        item.setDescription(description);

        // Then
        assertThat(item.getDescription()).isEqualTo(description);
    }

    @Test
    void testGetAndSetPrice() {
        // Given
        BigDecimal price = new BigDecimal("29.99");

        // When
        item.setPrice(price);

        // Then
        assertThat(item.getPrice()).isEqualByComparingTo(price);
    }

    @Test
    void testEqualsWithSameObject() {
        // Then
        assertThat(item).isEqualTo(item);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        Object other = new Object();

        // Then
        assertThat(item).isNotEqualTo(other);
    }

    @Test
    void testEqualsWithSameId() {
        // Given
        Item otherItem = new Item();
        otherItem.setId(1L);
        otherItem.setName("Different Name");
        otherItem.setDescription("Different Description");
        otherItem.setPrice(new BigDecimal("99.99"));

        // Then
        assertThat(item).isEqualTo(otherItem);
    }

    @Test
    void testEqualsWithDifferentId() {
        // Given
        Item otherItem = new Item();
        otherItem.setId(2L);
        otherItem.setName(item.getName());
        otherItem.setDescription(item.getDescription());
        otherItem.setPrice(item.getPrice());

        // Then
        assertThat(item).isNotEqualTo(otherItem);
    }

    @Test
    void testEqualsWithBothNullIds() {
        // Given
        Item item1 = new Item();
        Item item2 = new Item();

        // When
        item1.setId(null);
        item2.setId(null);

        // Then
        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void testEqualsWithOneNullId() {
        // Given
        Item item1 = new Item();
        Item item2 = new Item();

        // When
        item1.setId(null);
        item2.setId(1L);

        // Then
        assertThat(item1).isNotEqualTo(item2);
        assertThat(item2).isNotEqualTo(item1); // Test reversed comparison as well
    }

    @Test
    void testHashCodeWithSameId() {
        // Given
        Item otherItem = new Item();
        otherItem.setId(1L);

        // Then
        assertThat(item.hashCode()).isEqualTo(otherItem.hashCode());
    }

    @Test
    void testHashCodeWithDifferentId() {
        // Given
        Item otherItem = new Item();
        otherItem.setId(2L);

        // Then
        assertThat(item.hashCode()).isNotEqualTo(otherItem.hashCode());
    }

    @Test
    void testHashCodeWithNullId() {
        // Given
        Item item1 = new Item();
        Item item2 = new Item();

        // When
        item1.setId(null);
        item2.setId(null);

        // Then
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }
}
