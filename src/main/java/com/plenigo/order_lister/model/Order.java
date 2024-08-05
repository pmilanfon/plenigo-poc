package com.plenigo.order_lister.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String orderId;
    private String invoiceCustomerId;
    private List<OrderItem> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItem {
        private int position;
        private String title;
        private double price;
        private double tax;
    }
}
