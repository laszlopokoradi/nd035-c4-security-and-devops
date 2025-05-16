package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = EcommerceApplication.class)
@Transactional
class CartControllerTests {
    
    private final CartController cartController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    
    private User testUser;
    private Item testItem;
    private Authentication authentication;
    
    @Autowired
    public CartControllerTests(CartController cartController, 
                               UserRepository userRepository, 
                               ItemRepository itemRepository, 
                               CartRepository cartRepository) {
        this.cartController = cartController;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }
    
    @BeforeEach
    void setUp() {
        // Create test user with cart
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
        
        testUser.setCart(cart);
        cart.setUser(testUser);
        userRepository.save(testUser);
        
        // Create test item
        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test item description");
        testItem.setPrice(new BigDecimal("10.00"));
        itemRepository.save(testItem);
        
        // Mock authentication
        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), null, Collections.emptyList());
    }
    
    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }
    
    @Test
    void testAddToCart() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);
        
        // When
        ResponseEntity<Cart> response = cartController.addToCart(request, authentication);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getItems()).hasSize(2);
        assertThat(response.getBody().getItems()).allMatch(item -> item.getId().equals(testItem.getId()));
        assertThat(response.getBody().getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }
    
    @Test
    void testAddToCartWithInvalidItem() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(999L); // Non-existent item ID
        request.setQuantity(2);
        
        // When
        ResponseEntity<Cart> response = cartController.addToCart(request, authentication);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void testAddToCartWithInvalidUser() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);
        
        // Create authentication with non-existent username
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null, Collections.emptyList());
        
        // When
        ResponseEntity<Cart> response = cartController.addToCart(request, invalidAuth);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void testAddToCartWithNoAuthentication() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);
        
        // When
        ResponseEntity<Cart> response = cartController.addToCart(request, null);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    void testRemoveFromCart() {
        // First add items to cart
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setItemId(testItem.getId());
        addRequest.setQuantity(3);
        ResponseEntity<Cart> addResponse = cartController.addToCart(addRequest, authentication);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Given
        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setItemId(testItem.getId());
        removeRequest.setQuantity(2);
        
        // When
        ResponseEntity<Cart> response = cartController.removeFromCart(removeRequest, authentication);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getItems()).hasSize(1);
        assertThat(response.getBody().getTotal()).isEqualByComparingTo(new BigDecimal("10.00"));
    }
    
    @Test
    void testRemoveFromCartWithInvalidItem() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(999L); // Non-existent item ID
        request.setQuantity(1);
        
        // When
        ResponseEntity<Cart> response = cartController.removeFromCart(request, authentication);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void testRemoveFromCartWithInvalidUser() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);
        
        // Create authentication with non-existent username
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null, Collections.emptyList());
        
        // When
        ResponseEntity<Cart> response = cartController.removeFromCart(request, invalidAuth);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void testRemoveFromCartWithNoAuthentication() {
        // Given
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);
        
        // When
        ResponseEntity<Cart> response = cartController.removeFromCart(request, null);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
