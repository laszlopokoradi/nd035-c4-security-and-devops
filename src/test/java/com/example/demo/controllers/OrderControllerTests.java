package com.example.demo.controllers;


import static org.assertj.core.api.Assertions.*;

import com.example.demo.*;
import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.transaction.annotation.*;


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

        testItem = new Item();
        testItem.setName("Test Order Item");
        testItem.setDescription("Test order item description");
        testItem.setPrice(BigDecimal.valueOf(15.00));
        itemRepository.save(testItem);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), null, Collections.emptyList());
    }

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully.
    }

    @Test
    void testSubmitOrder() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);
        ResponseEntity<Cart> cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UserOrder> response = orderController.submit(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).hasSize(2);
        assertThat(response.getBody()
                           .getItems()).allMatch(item -> item.getId()
                                                             .equals(testItem.getId()));
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
        assertThat(response.getBody()
                           .getUser()
                           .getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void testSubmitOrderWithEmptyCart() {
        ResponseEntity<UserOrder> response = orderController.submit(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).isEmpty();
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testSubmitOrderWithNoAuthentication() {
        ResponseEntity<UserOrder> response = orderController.submit(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testSubmitOrderWithInvalidUser() {
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null,
                Collections.emptyList());

        ResponseEntity<UserOrder> response = orderController.submit(invalidAuth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetOrderHistory() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);
        ResponseEntity<Cart> cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<UserOrder> orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        cartResponse = cartController.addToCart(request, authentication);
        assertThat(cartResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        orderResponse = orderController.submit(authentication);
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<UserOrder>> historyResponse = orderController.getOrdersForUser(authentication);

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
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testGetOrderHistoryWithNoAuthentication() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetOrderHistoryWithInvalidUser() {
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null,
                Collections.emptyList());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(invalidAuth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
