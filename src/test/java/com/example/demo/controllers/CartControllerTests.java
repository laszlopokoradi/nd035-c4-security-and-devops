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
class CartControllerTests {

    private final CartController cartController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

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
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        testUser.setCart(cart);
        cart.setUser(testUser);
        userRepository.save(testUser);

        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test item description");
        testItem.setPrice(BigDecimal.valueOf(10.00));
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
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).hasSize(2);
        assertThat(response.getBody()
                           .getItems()).allMatch(item -> item.getId()
                                                             .equals(testItem.getId()));
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    }

    @Test
    void testAddToCartWithInvalidItem() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(999L); // Non-existent item ID
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAddToCartWithInvalidUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);

        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null,
                Collections.emptyList());

        ResponseEntity<Cart> response = cartController.addToCart(request, invalidAuth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAddToCartWithNoAuthentication() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addToCart(request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testRemoveFromCart() {
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setItemId(testItem.getId());
        addRequest.setQuantity(3);
        ResponseEntity<Cart> addResponse = cartController.addToCart(addRequest, authentication);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setItemId(testItem.getId());
        removeRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromCart(removeRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).hasSize(1);
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
    }

    @Test
    void testRemoveFromCartWithInvalidItem() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(999L); // Non-existent item ID
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testRemoveFromCartWithInvalidUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);

        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("nonExistentUser", null,
                Collections.emptyList());

        ResponseEntity<Cart> response = cartController.removeFromCart(request, invalidAuth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testRemoveFromCartWithNoAuthentication() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(testItem.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testRemoveFromEmptyCart() {
        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setItemId(testItem.getId());
        removeRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromCart(removeRequest, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getItems()).isEmpty();
        assertThat(response.getBody()
                           .getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
