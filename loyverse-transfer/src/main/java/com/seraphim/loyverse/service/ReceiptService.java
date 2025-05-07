package com.seraphim.loyverse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seraphim.loyverse.model.Receipt;
import com.seraphim.loyverse.model.ReceiptResponse;
import com.seraphim.loyverse.util.ItemVariantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService {

    //START: To Update Daily Before Running
    private String dateStartToTransfer = "2025-05-07";
    private String dateEndToTransfer = "2025-05-07";
    private String storeIdTarget = "9de98d8f-ff63-4a95-ac87-6851de6e5d7c";
    private String employeeIdTarget = "b306d937-b39a-4f2f-88ca-3f8222c5f008";
    private String posDeviceIdTarget = "b3212aed-e4ae-428f-8e1b-fa560bd1beec";
    private String seniorDiscount = "d685ecfd-4113-4713-b3d4-f4dec06dbb8f";
    private String paymentTypeCash = "d8d627f6-0c9e-4bf9-8738-b52e4d63abb7";
    private int skipReceiptCount = 2; //skip every x receipt count
    private double skipHigherAmount = 500;
    private double includeLowerAmount = 50;
    //END: To Update Daily Before Running

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${loyverse.api.url}")
    private String apiUrl;

    @Value("${loyverse.source.token}")
    private String sourceToken;

    @Value("${loyverse.destination.token}")
    private String targetToken;

    private List<Receipt> cachedReceipts = new ArrayList<>();

    public void transferReceipts() {
        log.info("Starting receipt transfer process...");

        cachedReceipts = fetchAllReceiptsFromSource();
        log.info("Fetched {} receipts from source", cachedReceipts.size());

        uploadReceiptsToTarget(cachedReceipts);
        log.info("Receipt transfer completed.");
    }

    private List<Receipt> fetchAllReceiptsFromSource() {
        List<Receipt> receipts = new ArrayList<>();
        String cursor = null;

        try {
            do {
                String url = apiUrl + "/receipts?";
                url += "created_at_min="+dateStartToTransfer+"T00:00:00.000Z&created_at_max="+dateEndToTransfer+"T23:59:59.000Z";
                if (cursor != null) {
                    url += "&cursor=" + cursor;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(sourceToken);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    ReceiptResponse receiptResponse = objectMapper.readValue(response.getBody(), ReceiptResponse.class);
                    if (receiptResponse.getReceipts() != null) {
                        receipts.addAll(receiptResponse.getReceipts());
                    }
                    cursor = receiptResponse.getCursor();
                } else {
                    log.error("Failed to fetch receipts: {}", response.getStatusCode());
                    break;
                }
            } while (cursor != null);
        } catch (Exception e) {
            log.error("Exception during receipt fetch: {}", e.getMessage(), e);
        }

        return receipts;
    }

    private void uploadReceiptsToTarget(List<Receipt> receipts) {
        String url = apiUrl + "/receipts";
        Receipt newReceipt = new Receipt();
        int counter = 0;

        for (Receipt receipt : receipts) {
            counter++;
            if ("REFUND".equals(receipt.getReceiptType())) {
                log.info("Skipping Refund receipt: {}", receipt.getReceiptNumber());
                counter--;
                continue;
            }
            if (counter == skipReceiptCount || receipt.getTotalMoney() > skipHigherAmount) {
                //dont skip if lower than amount
                if (counter == skipReceiptCount && receipt.getTotalMoney() < includeLowerAmount) {
                    log.info("Including receipt: {} instead of skip due to lower amount.", receipt.getReceiptNumber());
                    counter--; // bring back counter to previous value
                } else {
                    log.info("Skipping receipt: {}", receipt.getReceiptNumber());
                    counter = 0; // reset count
                    continue;
                }
            }
            try {
                newReceipt = new Receipt();
                //replace fixed Ids
                newReceipt.setStoreId(storeIdTarget);
                newReceipt.setEmployeeId(employeeIdTarget);
                newReceipt.setPosDeviceId(posDeviceIdTarget);
                newReceipt.setReceiptDate(receipt.getReceiptDate());
                if (receipt.getLineItems() != null) {
                    newReceipt.setLineItems(new ArrayList<Receipt.LineItem>());
                    for(Receipt.LineItem lineItem : receipt.getLineItems()) {
                        Receipt.LineItem newLineItem = new Receipt.LineItem();

//                        newLineItem.setVariantId("5586bef1-a34b-4a25-b25f-c062bdf915aa");
                        newLineItem.setVariantId(getItemVariantIdByItemName(lineItem.getItemName()));
                        newLineItem.setQuantity(lineItem.getQuantity());
                        newLineItem.setPrice(lineItem.getPrice());
                        newLineItem.setCost(lineItem.getCost());
                        newLineItem.setLineNote(lineItem.getLineNote());
                        if (lineItem.getLineDiscounts() != null) {
                            for(Receipt.LineItem.LineDiscount lineDiscount: lineItem.getLineDiscounts()) {
                                Receipt.LineItem.LineDiscount newLineDiscount = new Receipt.LineItem.LineDiscount();
                                newLineDiscount.setId(seniorDiscount); //Senior Discount
                                newLineItem.setLineDiscounts(new ArrayList<Receipt.LineItem.LineDiscount>());
                                newLineItem.getLineDiscounts().add(newLineDiscount);
                            }
                        }
                        newReceipt.getLineItems().add(newLineItem);
                    }
                }
                if (receipt.getPayments() != null) {
                    newReceipt.setPayments(new ArrayList<Receipt.Payment>());
                    for(Receipt.Payment payment : receipt.getPayments()) {
                        Receipt.Payment newPayment = new Receipt.Payment();
                        newPayment.setPaymentTypeId(paymentTypeCash); //Cash
                        newPayment.setMoneyAmount(payment.getMoneyAmount());
                        // newPayment.setName(payment.getName());
                        // newPayment.setType(payment.getType());
                        newPayment.setPaidAt(payment.getPaidAt());
                        newReceipt.getPayments().add(newPayment);
                    }
                }
                //end replace of fixed Ids
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(targetToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(newReceipt), headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Uploaded receipt: {}", receipt.getReceiptNumber());
                } else {
                    log.warn("Failed to upload receipt {}: {}", receipt.getReceiptNumber(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Error uploading receipt {}: {}", receipt.getReceiptNumber(), e.getMessage(), e);
            }
        }
    }

    private String getItemVariantIdByItemName(String itemName) {
        Map itemsMap = ItemVariantMapper.extractItemVariantMapFromResources();
        // itemsMap.put("17 Spicy Mango Dip (RMRS)", "7805ae58-3086-4973-b7df-0205c2681d62");

        if(itemsMap != null && itemsMap.get(itemName) != null) {
            List<String> variantIds = (List<String>) itemsMap.get(itemName);
            if (variantIds != null && variantIds.size() > 0) {
                return variantIds.getFirst();
            }
        }

        return "7805ae58-3086-4973-b7df-0205c2681d62"; // default to 17 Spicy Mango Dip (RMRS) from LV Report App
    }
}
