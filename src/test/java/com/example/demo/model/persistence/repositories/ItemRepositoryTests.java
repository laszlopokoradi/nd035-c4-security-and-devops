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
        item = new Item();
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(19.99));
        item.setDescription("Test item description");

        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void findByIdShouldReturnItem() {
        Optional<Item> foundItem = itemRepository.findById(item.getId());

        assertThat(foundItem).isPresent();
        assertThat(foundItem.get()
                            .getId()).isEqualTo(item.getId());
    }

    @Test
    void findByNameShouldReturnItems() {
        List<Item> foundItems = itemRepository.findByName("Test Item");

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.getFirst()
                             .getName()).isEqualTo(item.getName());
    }

    @Test
    void saveShouldPersistItem() {
        Item newItem = new Item();
        newItem.setName("New Item");
        newItem.setPrice(BigDecimal.valueOf(29.99));
        newItem.setDescription("New item description");

        Item savedItem = itemRepository.save(newItem);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("New Item");
    }

    @Test
    void deleteShouldRemoveItem() {
        Long itemId = item.getId();

        itemRepository.delete(item);
        Optional<Item> deletedItem = itemRepository.findById(itemId);

        assertThat(deletedItem).isNotPresent();
    }
}