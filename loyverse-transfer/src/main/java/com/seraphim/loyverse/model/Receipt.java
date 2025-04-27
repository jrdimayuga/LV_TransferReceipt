package com.seraphim.loyverse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class Receipt {

    @JsonProperty("receipt_number")
    private String receiptNumber;

    private String note;

    @JsonProperty("receipt_type")
    private String receiptType;

    @JsonProperty("refund_for")
    private String refundFor;

    private String order;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    private String source;

    @JsonProperty("receipt_date")
    private ZonedDateTime receiptDate;

    @JsonProperty("cancelled_at")
    private ZonedDateTime cancelledAt;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("total_tax")
    private Double totalTax;

    @JsonProperty("points_earned")
    private Double pointsEarned;

    @JsonProperty("points_deducted")
    private Double pointsDeducted;

    @JsonProperty("points_balance")
    private Double pointsBalance;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("total_discount")
    private Double totalDiscount;

    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("pos_device_id")
    private String posDeviceId;

    @JsonProperty("dining_option")
    private String diningOption;

    @JsonProperty("total_discounts")
    private List<Object> totalDiscounts;

    @JsonProperty("total_taxes")
    private List<Object> totalTaxes;

    private Double tip;
    private Double surcharge;

    @JsonProperty("line_items")
    private List<LineItem> lineItems;

    private List<Payment> payments;

    @Data
    public static class LineItem {
        private String id;

        @JsonProperty("item_id")
        private String itemId;

        @JsonProperty("variant_id")
        private String variantId;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("variant_name")
        private String variantName;

        private String sku;
        private Integer quantity;
        private Double price;

        @JsonProperty("gross_total_money")
        private Double grossTotalMoney;

        @JsonProperty("total_money")
        private Double totalMoney;

        private Double cost;

        @JsonProperty("cost_total")
        private Double costTotal;

        @JsonProperty("line_note")
        private String lineNote;

        @JsonProperty("line_taxes")
        private List<Object> lineTaxes;

        @JsonProperty("total_discount")
        private Double totalDiscount;

        @JsonProperty("line_discounts")
        private List<LineDiscount> lineDiscounts;

        @JsonProperty("line_modifiers")
        private List<Object> lineModifiers;

        @Data
        public static class LineDiscount {
            private String id;
        }

    }



    @Data
    public static class Payment {
        @JsonProperty("payment_type_id")
        private String paymentTypeId;

        private String name;
        private String type;

        @JsonProperty("money_amount")
        private Double moneyAmount;

        @JsonProperty("paid_at")
        private ZonedDateTime paidAt;

        @JsonProperty("payment_details")
        private Object paymentDetails;
    }
}
