package com.example.demo.controllers;


import com.example.demo.*;
import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.transaction.annotation.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = EcommerceApplication.class)
@Transactional
class OrderControllerTests {

    private final OrderController orderController;
    private final CartController cartController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    private User testUser;
    private Item testItem;
    private Authentication authentication;

    @Autowired
    public OrderControllerTests(OrderController orderController,
                                CartController cartController,
                                UserRepository userRepository,
                                ItemRepository itemRepository,
                                CartRepository cartRepository) {
        this.orderController = orderController;
        this.cartController = cartController;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @BeforeEach
    void setUp() {
        // Create test user with cart
        testUser = new User();
        testUser.setUsername("testOrderUser");
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
        testItem.setName("Test Order Item");
        testItem.setDescription("Test order item description");
        testItem.setPrice(new BigDecimal("15.00"));
        itemRepository.save(testItem);

        // Mock authentication
        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), null, Collections.emptyList());
    }

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testSubmitOrder() {
        // Add items to cart first
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);
        ResponseEntity<Cart> cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When
        ResponseEntity<UserOrder> response = orderController.submit(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).hasSize(2);
        assertThat(response.getBody()
                           .getItems()).allMatch(item -> item.getId()
                                                             .equals(testItem.getId()));
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(response.getBody()
                           .getUser()
                           .getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void testSubmitOrderWithEmptyCart() {
        // When - submit order with empty cart
        ResponseEntity<UserOrder> response = orderController.submit(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).isEmpty();
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testSubmitOrderWithNoAuthentication() {
        // When
        ResponseEntity<UserOrder> response = orderController.submit(null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testSubmitOrderWithInvalidUser() {
        // Create authentication with non-existent username
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null, Collections.emptyList());

        // When
        ResponseEntity<UserOrder> response = orderController.submit(invalidAuth);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetOrderHistory() {
        // Add items to cart first
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);
        ResponseEntity<Cart> cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Submit an order
        ResponseEntity<UserOrder> orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Add more items and submit another order
        cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When
        ResponseEntity<List<UserOrder>> historyResponse = orderController.getOrdersForUser(authentication);

        // Then
        assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(historyResponse.getBody()).isNotNull();
        assertThat(historyResponse.getBody()).hasSize(2);
        assertThat(historyResponse.getBody()
                                  .get(0)
                                  .getUser()
                                  .getUsername()).isEqualTo(testUser.getUsername());
        assertThat(historyResponse.getBody()
                                  .get(1)
                                  .getUser()
                                  .getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void testGetOrderHistoryWithNoOrders() {
        // When - get order history without placing any orders
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testGetOrderHistoryWithNoAuthentication() {
        // When
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetOrderHistoryWithInvalidUser() {
        // Create authentication with non-existent username
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null, Collections.emptyList());

        // When
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(invalidAuth);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
