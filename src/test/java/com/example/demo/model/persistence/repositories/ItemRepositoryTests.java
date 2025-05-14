package com.example.demo.model.persistence.repositories;


import com.example.demo.model.persistence.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class ItemRepositoryTests {

    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRepositoryTests(TestEntityManager entityManager, ItemRepository itemRepository) {
        this.entityManager = entityManager;
        this.itemRepository = itemRepository;
    }

    private Item item;

    @BeforeEach
    void setUp() {
        // Create a test item
        item = new Item();
        item.setName("Test Item");
        item.setPrice(new BigDecimal("19.99"));
        item.setDescription("Test item description");

        // Save the item to the in-memory database
        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void findByIdShouldReturnItem() {
        // When
        Optional<Item> foundItem = itemRepository.findById(item.getId());

        // Then
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get()
                            .getId()).isEqualTo(item.getId());
    }

    @Test
    void findByNameShouldReturnItems() {
        // When
        List<Item> foundItems = itemRepository.findByName("Test Item");

        // Then
        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.getFirst()
                             .getName()).isEqualTo(item.getName());
    }

    @Test
    void saveShouldPersistItem() {
        // Given
        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setPrice(new BigDecimal("29.99"));
        newItem.setDescription("New item description");

        // When
        Item savedItem = itemRepository.save(newItem);

        // Then
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("New Item");
    }

    @Test
    void deleteShouldRemoveItem() {
        // Given
        Long itemId = item.getId();

        // When
        itemRepository.delete(item);
        Optional<Item> deletedItem = itemRepository.findById(itemId);

        // Then
        assertThat(deletedItem).isNotPresent();
    }
}