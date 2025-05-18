package com.example.demo.controllers;


import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import java.util.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemRepository itemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        LOGGER.atDebug().log(() -> "ItemController.getItems() called");
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        LOGGER.atDebug().log(() -> "ItemController.getItemById() called");
        return ResponseEntity.of(itemRepository.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        LOGGER.atDebug().log(() -> "ItemController.getItemsByName() called");
        List<Item> items = itemRepository.findByName(name);

        return items == null || items.isEmpty() ? ResponseEntity.notFound()
                                                                .build()
                : ResponseEntity.ok(items);

    }

}
