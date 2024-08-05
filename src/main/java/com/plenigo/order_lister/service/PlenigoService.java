package com.plenigo.order_lister.service;

import com.plenigo.order_lister.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlenigoService {

    private static final int TOTAL_LIMIT = 300;
    private static final int MAX_SIZE = 100;
    private final RestClient restClient;
    private final CsvService csvService;

    public void processOrders() {
        String startingAfter = null;
        int fetchedOrders = 0;

        while (fetchedOrders < TOTAL_LIMIT) {
            List<Order> orders = fetchOrdersBatch(startingAfter);
            if (orders.isEmpty()) {
                break;
            }

            startingAfter = orders.get(orders.size() - 1).getOrderId(); // Update the cursor for the next batch
            fetchedOrders += orders.size();

            // Process the current batch of orders asynchronously
            List<CompletableFuture<OrderAggregate>> futures = orders
                    .stream()
                    .map(order -> {
                        CompletableFuture<Customer> customerFuture = getCustomerAsync(order.getInvoiceCustomerId());
                        CompletableFuture<Optional<Invoice>> invoicesFuture = getInvoiceAsync(order.getOrderId());

                        return customerFuture.thenCombine(invoicesFuture, (customer, invoice) -> {

                            if (invoice.isPresent()) {
                                return OrderAggregate.from(order, customer, invoice.get());
                            } else {
                                return OrderAggregate.from(order, customer);
                            }
                        });
                    }).flatMap(future -> future.thenApply(List::stream).join())
                    .map(orderAggregateFuture -> CompletableFuture.supplyAsync(() -> orderAggregateFuture))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join) // Join to get the result of each future
                            .collect(Collectors.toList()) // Collect results into a list
            ).thenAccept(csvService::writeToCsv).exceptionally(ex -> {
                // Handle any exceptions that occurred during the process
                log.error("Error while writing to CSV {}", ex.getMessage(), ex);
                return null;
            });

            if (futures.size() < MAX_SIZE) {
                break;
            }
        }

        log.info("Finished processing all orders");
    }

    public CompletableFuture<Customer> getCustomerAsync(String customerId) {
        return CompletableFuture.supplyAsync(() ->
                restClient.get()
                        .uri("/customers/{customerId}", customerId)
                        .retrieve()
                        .body(Customer.class)
        );
    }

    public CompletableFuture<Optional<Invoice>> getInvoiceAsync(String orderId) {
        return CompletableFuture.supplyAsync(() -> fetchInvoiceByOrderId(orderId));
    }

    private List<Order> fetchOrdersBatch(String startingAfter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/orders")
                        .queryParam("size", MAX_SIZE)
                        .queryParamIfPresent("startingAfter", startingAfter != null ? Optional.of(startingAfter) : Optional.empty())
                        .build())
                .retrieve()
                .body(Orders.class)
                .getItems();
    }

    private Optional<Invoice> fetchInvoiceByOrderId(String orderId) {
        List<Invoice> invoices = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/invoices").queryParam("orderId", orderId).build())
                .retrieve()
                .body(Invoices.class)
                .getItems();

        if (CollectionUtils.isEmpty(invoices)) {
            return Optional.empty();
        } else {
            return Optional.of(invoices.get(0));
        }
    }
}
