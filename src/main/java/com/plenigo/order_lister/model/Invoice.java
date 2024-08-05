package com.plenigo.order_lister.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    private String invoiceId;
    private String invoiceDate;
}
