package com.campuscart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Data Transfer Object for creating an order during checkout.
 */
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Student ID / Roll number is required")
    private String studentId;

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Hostel / Room or delivery location is required")
    private String hostelRoom;

    private String paymentMethod = "Cash on Delivery";

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<CartItemDto> items;

    public OrderRequest() {
    }

    public OrderRequest(String customerName, String studentId, String email, String hostelRoom, 
                        String paymentMethod, List<CartItemDto> items) {
        this.customerName = customerName;
        this.studentId = studentId;
        this.email = email;
        this.hostelRoom = hostelRoom;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHostelRoom() {
        return hostelRoom;
    }

    public void setHostelRoom(String hostelRoom) {
        this.hostelRoom = hostelRoom;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }
}
