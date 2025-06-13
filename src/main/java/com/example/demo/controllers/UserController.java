package com.example.demo.controllers;


import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        LOGGER.atDebug()
              .setMessage(() -> "UserController.findById() called")
              .log();
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        LOGGER.atDebug()
              .setMessage(() -> "UserController.findByUserName() called")
              .log();
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound()
                                            .build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        LOGGER.atDebug()
              .log(() -> "UserController.createUser() called");
        LOGGER.atInfo()
              .setMessage(() -> "Creating user with username: {}")
              .addArgument(createUserRequest.getUsername())
              .log();
        User user = new User();
        user.setUsername(createUserRequest.getUsername());

        if (!createUserRequest.isPasswordValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            LOGGER.atError().setMessage(() -> "Error creating user: " + e.getMessage()).log();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .build();
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        user.setCart(cart);

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            LOGGER.atError().setMessage(() -> "Error creating user: " + e.getMessage()).log();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(user);
    }
}
