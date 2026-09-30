package com.campuscart.service;

import com.campuscart.dto.CartItemDto;
import com.campuscart.dto.OrderRequest;
import com.campuscart.model.Order;
import com.campuscart.repository.OrderRepository;
import com.campuscart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        orderRepository = new OrderRepository();
        orderService = new OrderService(orderRepository, productRepository);
    }

    @Test
    @DisplayName("createOrder correctly calculates total and populates order fields")
    void testCreateOrderSuccess() {
        // Product 1: Spiral Notebook = 60.0, Product 6: Scientific Calculator = 450.0
        OrderRequest request = new OrderRequest(
                "Aman Verma",
                "22BCSE101",
                "aman@campus.edu",
                "Hostel D, Room 105",
                "Cash on Delivery",
                List.of(
                        new CartItemDto(1L, 2), // 2 * 60 = 120
                        new CartItemDto(6L, 1)  // 1 * 450 = 450
                )
        );

        Order order = orderService.createOrder(request);

        assertNotNull(order);
        assertTrue(order.getOrderId().startsWith("CC-"));
        assertEquals("Aman Verma", order.getCustomerName());
        assertEquals("22BCSE101", order.getStudentId());
        assertEquals(570.0, order.getTotalAmount());
        assertEquals(2, order.getItems().size());
        assertEquals("CONFIRMED", order.getStatus());
    }

    @Test
    @DisplayName("createOrder throws exception when product ID is invalid")
    void testCreateOrderInvalidProduct() {
        OrderRequest request = new OrderRequest(
                "Aman Verma",
                "22BCSE101",
                "aman@campus.edu",
                "Hostel D, Room 105",
                "Cash on Delivery",
                List.of(new CartItemDto(9999L, 1))
        );

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }
}
