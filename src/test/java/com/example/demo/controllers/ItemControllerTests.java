package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        // Create test items
        testItem1 = new Item();
        testItem1.setName("Test Item 1");
        testItem1.setDescription("This is test item 1");
        testItem1.setPrice(new BigDecimal("19.99"));
        
        testItem2 = new Item();
        testItem2.setName("Test Item 2");
        testItem2.setDescription("This is test item 2");
        testItem2.setPrice(new BigDecimal("29.99"));
        
        // Save items to the repository
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
    }

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testGetItems() {
        // When
        ResponseEntity<List<Item>> response = itemController.getItems();
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
        
        // Verify our test items are in the response
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
        // Given
        Long itemId = testItem1.getId();
        
        // When
        ResponseEntity<Item> response = itemController.getItemById(itemId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(itemId);
        assertThat(response.getBody().getName()).isEqualTo(testItem1.getName());
        assertThat(response.getBody().getDescription()).isEqualTo(testItem1.getDescription());
        assertThat(response.getBody().getPrice()).isEqualByComparingTo(testItem1.getPrice());
    }
    
    @Test
    void testGetItemByIdNotFound() {
        // Given a non-existent item ID
        Long nonExistentId = 999L;
        
        // When
        ResponseEntity<Item> response = itemController.getItemById(nonExistentId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
    
    @Test
    void testGetItemsByName() {
        // Given
        String itemName = testItem1.getName();
        
        // When
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        
        List<Item> items = response.getBody();
        assertThat(items).extracting(Item::getName)
                .containsOnly(itemName);
    }
    
    @Test
    void testGetItemsByNameNotFound() {
        // Given a non-existent item name
        String nonExistentName = "NonExistentItem";
        
        // When
        ResponseEntity<List<Item>> response = itemController.getItemsByName(nonExistentName);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
