package com.seraphim.loyverse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReceiptResponse {

    @JsonProperty("receipts")
    private List<Receipt> receipts;

    @JsonProperty("cursor")
    private String cursor;
}
