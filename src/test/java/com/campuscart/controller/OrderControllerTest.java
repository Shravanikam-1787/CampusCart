package com.campuscart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.campuscart.dto.CartItemDto;
import com.campuscart.dto.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/orders successfully creates an order")
    void testCreateOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest(
                "Rahul Sharma",
                "21BCE1042",
                "rahul.sharma@college.edu",
                "Hostel C, Room 204",
                "Cash on Delivery",
                List.of(
                        new CartItemDto(1L, 2), // 2 * 60 = 120
                        new CartItemDto(2L, 3)  // 3 * 10 = 30
                )
        );

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", startsWith("CC-")))
                .andExpect(jsonPath("$.customerName", is("Rahul Sharma")))
                .andExpect(jsonPath("$.studentId", is("21BCE1042")))
                .andExpect(jsonPath("$.totalAmount", is(150.0)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andReturn();

        // Also test GET /api/orders/{orderId}
        String responseJson = result.getResponse().getContentAsString();
        String orderId = objectMapper.readTree(responseJson).get("orderId").asText();

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(orderId)))
                .andExpect(jsonPath("$.customerName", is("Rahul Sharma")));
    }

    @Test
    @DisplayName("POST /api/orders with empty fields returns 400 Bad Request")
    void testCreateOrder_ValidationFailure() throws Exception {
        OrderRequest invalidRequest = new OrderRequest(
                "",
                "",
                "invalid-email",
                "",
                "Cash on Delivery",
                List.of()
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders with non-existent product ID returns 400 Bad Request")
    void testCreateOrder_ProductNotFound() throws Exception {
        OrderRequest request = new OrderRequest(
                "Priya Patel",
                "21BCE1099",
                "priya@college.edu",
                "Hostel A, Room 102",
                "Pay at Counter",
                List.of(new CartItemDto(9999L, 1))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Product not found")));
    }
}
