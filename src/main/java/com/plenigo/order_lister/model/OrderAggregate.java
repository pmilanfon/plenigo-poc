package com.plenigo.order_lister.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@Builder
public class OrderAggregate {
    private String orderId;
    private int orderItemPosition;
    private String title;
    private double price;
    private double taxRate;
    private String customerId;
    private String customerEmail;
    private String customerCreationDate;
    private String invoiceId;
    private String invoiceDate;

    public static List<OrderAggregate> from(Order order, Customer customer, Invoice invoice) {
        Objects.requireNonNull(order, "order cannot be null");

        return order.getItems().stream()
                .map(orderItem -> new OrderAggregate(order.getOrderId(), orderItem.getPosition(), orderItem.getTitle(), orderItem.getPrice(), orderItem.getTax(), order.getInvoiceCustomerId(), customer.getEmail(), customer.getBirthday(), invoice.getInvoiceId(), invoice.getInvoiceDate()))
                .toList();
    }

    public static List<OrderAggregate> from(Order order, Customer customer) {
        return order.getItems().stream()
                .map(orderItem -> new OrderAggregate(order.getOrderId(), orderItem.getPosition(), orderItem.getTitle(), orderItem.getPrice(), orderItem.getTax(), order.getInvoiceCustomerId(), customer.getEmail(), customer.getBirthday(), null, null))
                .toList();
    }
}
