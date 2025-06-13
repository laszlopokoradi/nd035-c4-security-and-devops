package com.example.demo.controllers;


import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;


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
        LOGGER.atDebug()
              .setMessage(() -> "CartController.addToCart() called")
              .log();
        if (authentication == null) {
            LOGGER.atWarn()
                  .setMessage(() -> "No authentication found in request \"POST addToCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            LOGGER.atWarn()
                  .setMessage(() -> "User not found in request to \"POST addToCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) {
            LOGGER.atWarn()
                  .setMessage(() -> "Item not found in request to \"POST addToCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                 .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);
        LOGGER.atInfo()
              .setMessage("Cart updated with item {} and quantity {}")
              .addArgument(item.get().getId())
              .addArgument(request.getQuantity())
              .log();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request, Authentication authentication) {
        LOGGER.atDebug()
              .setMessage(() -> "CartController.removeFromCart() called")
              .log();
        if (authentication == null) {
            LOGGER.atWarn()
                  .setMessage(() -> "No authentication found in request \"POST removeFromCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            LOGGER.atWarn()
                  .setMessage(() -> "User not found in request to \"POST removeFromCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) {
            LOGGER.atWarn()
                  .setMessage(() -> "Item not found in request to \"POST removeFromCart\"")
                  .log();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                 .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);
        LOGGER.atInfo()
              .setMessage("Item {} and quantity {} were removed from cart")
              .addArgument(item.get().getId())
              .addArgument(request.getQuantity())
              .log();
        return ResponseEntity.ok(cart);
    }

}
