package com.example.demo.integration;

import com.example.demo.EcommerceApplication;
import com.example.demo.controllers.*;
import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.authentication.*;
import org.springframework.transaction.annotation.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the complete user journey from registration to checkout.
 */
@SpringBootTest(classes = EcommerceApplication.class)
@Transactional
class UserJourneyIntegrationTests {
    @Autowired
    private UserController userController;
    
    @Autowired
    private CartController cartController;
    
    @Autowired
    private OrderController orderController;
    
    @Autowired
    private ItemController itemController;
    
    @Autowired
    private ItemRepository itemRepository;
    
    private User testUser;
    private Authentication authentication;
    
    @BeforeEach
    void setUp() {
        // Create a test user
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("journeyUser");
        createUserRequest.setPassword("TestP@ssw0rd");
        createUserRequest.setRepeatedPassword("TestP@ssw0rd");
        
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        testUser = response.getBody();
        assertThat(testUser).isNotNull();
        
        // Create authentication for the user
        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), null, Collections.emptyList());
    }
    
    @Test
    void testCompleteUserJourney() {
        // Step 1: Verify user was created successfully
        ResponseEntity<User> userResponse = userController.findByUserName(testUser.getUsername());
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponse.getBody()).isNotNull();
        assertThat(userResponse.getBody().getUsername()).isEqualTo(testUser.getUsername());
        
        // Step 2: Get available items
        ResponseEntity<List<Item>> itemsResponse = itemController.getItems();
        assertThat(itemsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(itemsResponse.getBody()).isNotNull();
        assertThat(itemsResponse.getBody()).isNotEmpty();
        
        // Step 3: Add items to cart
        Item item = itemsResponse.getBody().getFirst();
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setItemId(item.getId());
        addRequest.setQuantity(2);
        
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(addRequest, authentication);
        assertThat(addToCartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addToCartResponse.getBody()).isNotNull();
        assertThat(addToCartResponse.getBody().getItems()).hasSize(2);
        assertThat(addToCartResponse.getBody().getTotal()).isEqualTo(item.getPrice().multiply(BigDecimal.valueOf(2)));
        
        // Step 4: Submit order
        ResponseEntity<UserOrder> orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderResponse.getBody()).isNotNull();
        assertThat(orderResponse.getBody().getItems()).hasSize(2);
        assertThat(orderResponse.getBody().getTotal()).isEqualTo(item.getPrice().multiply(BigDecimal.valueOf(2)));
        assertThat(orderResponse.getBody().getUser().getUsername()).isEqualTo(testUser.getUsername());
        
        // Step 5: Verify order history
        ResponseEntity<List<UserOrder>> historyResponse = orderController.getOrdersForUser(authentication);
        assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(historyResponse.getBody()).isNotNull();
        assertThat(historyResponse.getBody()).hasSize(1);
        assertThat(historyResponse.getBody().getFirst().getUser().getUsername()).isEqualTo(testUser.getUsername());
    }
    
    @Test
    void testUserJourneyWithCartModifications() {
        // Step 1: Get available items
        ResponseEntity<List<Item>> itemsResponse = itemController.getItems();
        assertThat(itemsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(itemsResponse.getBody()).isNotNull();
        assertThat(itemsResponse.getBody()).isNotEmpty();
        
        Item item = itemsResponse.getBody().getFirst();
        
        // Step 2: Add items to cart
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setItemId(item.getId());
        addRequest.setQuantity(3);
        
        ResponseEntity<Cart> addToCartResponse = cartController.addToCart(addRequest, authentication);
        assertThat(addToCartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addToCartResponse.getBody()).isNotNull();
        assertThat(addToCartResponse.getBody().getItems()).hasSize(3);
        
        // Step 3: Remove an item from cart
        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setItemId(item.getId());
        removeRequest.setQuantity(1);
        
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromCart(removeRequest, authentication);
        assertThat(removeFromCartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(removeFromCartResponse.getBody()).isNotNull();
        assertThat(removeFromCartResponse.getBody().getItems()).hasSize(2);
        
        // Step 4: Submit order
        ResponseEntity<UserOrder> orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderResponse.getBody()).isNotNull();
        assertThat(orderResponse.getBody().getItems()).hasSize(2);
    }
}