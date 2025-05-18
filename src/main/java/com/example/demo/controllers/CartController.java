package com.example.demo.controllers;

import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request, Authentication authentication) {
        LOGGER.atDebug().log(() -> "CartController.addToCart() called");
        if (authentication == null) {
            LOGGER.atInfo().log(() -> "No authentication found in request to addToCart");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            LOGGER.info("User not found in request to addToCart");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) {
            LOGGER.info("Item not found in request to addToCart");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                 .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        LOGGER.info("Cart updated with item {} and quantity {}", item.get().getName(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request, Authentication authentication) {
        LOGGER.atDebug().log(() -> "CartController.removeFromCart() called");
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                 .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

}
