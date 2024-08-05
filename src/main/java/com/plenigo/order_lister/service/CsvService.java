package com.plenigo.order_lister.service;

import com.plenigo.order_lister.model.OrderAggregate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@Slf4j
public class CsvService {

    private static final String[] CSV_COLUMNS = {"OrderId", "OrderItemPosition", "Title", "Price", "TaxRate", "CustomerId", "CustomerEmail", "CustomerCreationDate", "InvoiceId", "InvoiceDate"};
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(CSV_COLUMNS)
            .build();
    private static final String CSV_FILE_PATH = "orders.csv";

    @PostConstruct
    public void init() {
        createCsvFileWithHeaders();
    }

    public void writeToCsv(List<OrderAggregate> orderList) {
        log.info("Appending {} orders to CSV", orderList.size());
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH),
                StandardOpenOption.APPEND);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (OrderAggregate data : orderList) {
                csvPrinter.printRecord(
                        data.getOrderId(),
                        data.getOrderItemPosition(),
                        data.getTitle(),
                        data.getPrice(),
                        data.getTaxRate(),
                        data.getCustomerId(),
                        data.getCustomerEmail(),
                        data.getCustomerCreationDate(),
                        data.getInvoiceId(),
                        data.getInvoiceDate());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error appending to CSV", e);
        }
        log.info("Appending to CSV finished!");
    }

    private void createCsvFileWithHeaders() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSV_FORMAT)) {
            log.info("CSV file created with headers.");
        } catch (IOException e) {
            throw new RuntimeException("Error creating CSV file with headers", e);
        }
    }
}