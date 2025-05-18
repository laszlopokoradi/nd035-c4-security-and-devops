package com.example.demo.controllers;

import static org.assertj.core.api.Assertions.*;

import com.example.demo.*;
import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest(classes = EcommerceApplication.class)
@Transactional
class ItemControllerTests {

    private final ItemController itemController;
    private final ItemRepository itemRepository;

    private Item testItem1;
    private Item testItem2;

    @Autowired
    public ItemControllerTests(ItemController itemController, ItemRepository itemRepository) {
        this.itemController = itemController;
        this.itemRepository = itemRepository;
    }

    @BeforeEach
    void setUp() {
        testItem1 = new Item();
        testItem1.setName("Test Item 1");
        testItem1.setDescription("This is test item 1");
        testItem1.setPrice(BigDecimal.valueOf(19.99));

        testItem2 = new Item();
        testItem2.setName("Test Item 2");
        testItem2.setDescription("This is test item 2");
        testItem2.setPrice(BigDecimal.valueOf(29.99));

        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
    }

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testGetItems() {
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);

        List<Item> items = response.getBody();
        assertThat(items).extracting(Item::getName)
                         .contains(testItem1.getName(), testItem2.getName());
        assertThat(items).extracting(Item::getDescription)
                         .contains(testItem1.getDescription(), testItem2.getDescription());
        assertThat(items).extracting(Item::getPrice)
                         .contains(testItem1.getPrice(), testItem2.getPrice());
    }

    @Test
    void testGetItemById() {
        Long itemId = testItem1.getId();

        ResponseEntity<Item> response = itemController.getItemById(itemId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(itemId);
        assertThat(response.getBody().getName()).isEqualTo(testItem1.getName());
        assertThat(response.getBody().getDescription()).isEqualTo(testItem1.getDescription());
        assertThat(response.getBody().getPrice()).isEqualByComparingTo(testItem1.getPrice());
    }

    @Test
    void testGetItemByIdNotFound() {
        Long nonExistentId = 999L;

        ResponseEntity<Item> response = itemController.getItemById(nonExistentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetItemsByName() {
        String itemName = testItem1.getName();

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();

        List<Item> items = response.getBody();
        assertThat(items).extracting(Item::getName)
                         .containsOnly(itemName);
    }

    @Test
    void testGetItemsByNameNotFound() {
        String nonExistentName = "NonExistentItem";

        ResponseEntity<List<Item>> response = itemController.getItemsByName(nonExistentName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
