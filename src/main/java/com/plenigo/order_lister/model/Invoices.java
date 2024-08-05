package com.plenigo.order_lister.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoices {
    private List<Invoice> items = new ArrayList<>();
}
