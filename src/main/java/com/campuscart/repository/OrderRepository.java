package com.campuscart.repository;

import com.campuscart.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory repository for storing customer orders.
 */
@Repository
public class OrderRepository {

    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();
    private final AtomicLong orderCounter = new AtomicLong(1000);

    public String generateNextOrderId() {
        return "CC-" + orderCounter.incrementAndGet();
    }

    public Order save(Order order) {
        orderMap.put(order.getOrderId(), order);
        return order;
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orderMap.get(orderId));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }
}
