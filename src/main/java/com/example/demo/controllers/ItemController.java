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

    @GetMapping({"", "/"})
    public ResponseEntity<List<Item>> getItems() {
        LOGGER.atDebug().setMessage(() -> "ItemController.getItems() called").log();
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        LOGGER.atDebug().setMessage(() -> "ItemController.getItemById() called").log();
        return ResponseEntity.of(itemRepository.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        LOGGER.atDebug().setMessage(() -> "ItemController.getItemsByName() called").log();
        List<Item> items = itemRepository.findByName(name);

        return items == null || items.isEmpty() ? ResponseEntity.notFound()
                                                                .build()
                : ResponseEntity.ok(items);

    }

}
