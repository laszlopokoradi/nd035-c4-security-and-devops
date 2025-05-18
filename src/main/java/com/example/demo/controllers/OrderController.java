package com.example.demo.controllers;


import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import java.util.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    public OrderController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<UserOrder> submit(Authentication authentication) {
        LOGGER.atDebug().log(() -> "OrderController.submit() called");
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/history")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(Authentication authentication) {
        LOGGER.atDebug().log(() -> "OrderController.getOrdersForUser() called");
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(orderRepository.findByUser(user));
    }
}
