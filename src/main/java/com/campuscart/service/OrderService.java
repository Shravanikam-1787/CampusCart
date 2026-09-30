package com.campuscart.service;

import com.campuscart.dto.CartItemDto;
import com.campuscart.dto.OrderRequest;
import com.campuscart.model.Order;
import com.campuscart.model.OrderItem;
import com.campuscart.model.Product;
import com.campuscart.repository.OrderRepository;
import com.campuscart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer managing order placement and retrieval.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(OrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + itemDto.getProductId()));

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemDto.getQuantity()
            );

            orderItems.add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        String orderId = orderRepository.generateNextOrderId();
        Order order = new Order(
                orderId,
                request.getCustomerName(),
                request.getStudentId(),
                request.getEmail(),
                request.getHostelRoom(),
                request.getPaymentMethod() != null ? request.getPaymentMethod() : "Cash on Delivery",
                orderItems,
                Math.round(totalAmount * 100.0) / 100.0,
                LocalDateTime.now(),
                "CONFIRMED"
        );

        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
