package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTests {

    private final TestEntityManager entityManager;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderRepositoryTests(TestEntityManager entityManager, OrderRepository orderRepository) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
    }

    private User user;
    private UserOrder order;
    private Item item;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUsername("orderUser");
        user.setPassword("password");

        // Create test item
        item = new Item();
        item.setName("Order Item");
        item.setPrice(new BigDecimal("10.00"));
        item.setDescription("Order item description");

        // Create test order
        order = new UserOrder();
        order.setUser(user);
        order.setItems(List.of(item));
        order.setTotal(item.getPrice());

        // Persist entities
        entityManager.persist(item);
        entityManager.persist(user);
        entityManager.persist(order);
        entityManager.flush();
    }

    @Test
    void findByUserShouldReturnOrders() {
        List<UserOrder> foundOrders = orderRepository.findByUser(user);

        assertThat(foundOrders).isNotEmpty();
        assertThat(foundOrders.getFirst().getUser()).isEqualTo(user);
    }

    @Test
    void findByIdShouldReturnOrder() {
        Optional<UserOrder> foundOrder = orderRepository.findById(order.getId());

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void saveShouldPersistOrder() {
        UserOrder newOrder = new UserOrder();
        newOrder.setUser(user);
        newOrder.setItems(List.of(item));
        newOrder.setTotal(new BigDecimal("20.00"));

        UserOrder savedOrder = orderRepository.save(newOrder);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getUser()).isEqualTo(user);
        assertThat(savedOrder.getItems()).contains(item);
    }

    @Test
    void deleteShouldRemoveOrder() {
        Long orderId = order.getId();

        orderRepository.delete(order);
        Optional<UserOrder> deletedOrder = orderRepository.findById(orderId);

        assertThat(deletedOrder).isNotPresent();
    }
}