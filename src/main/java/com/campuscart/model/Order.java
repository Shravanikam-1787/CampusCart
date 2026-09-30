package com.campuscart.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a placed order in CampusCart.
 */
public class Order {
    private String orderId;
    private String customerName;
    private String studentId;
    private String email;
    private String hostelRoom;
    private String paymentMethod;
    private List<OrderItem> items = new ArrayList<>();
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;

    public Order() {
    }

    public Order(String orderId, String customerName, String studentId, String email, 
                 String hostelRoom, String paymentMethod, List<OrderItem> items, 
                 double totalAmount, LocalDateTime orderDate, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.studentId = studentId;
        this.email = email;
        this.hostelRoom = hostelRoom;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
