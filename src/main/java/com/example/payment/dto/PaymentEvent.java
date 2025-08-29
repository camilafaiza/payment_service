package com.example.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEvent {
    public String eventId;
    public String eventType; // payment_authorized|payment_captured|refund_issued
    public Instant eventTime;
    public String paymentId;
    public String orderId;
    public String userId;
    public String merchantId;
    public long amount;
    public String currency; // IDR
    public String method;   // card|ewallet|va|qris
    public String status;   // authorized|captured|failed|refunded
    @JsonProperty("attributes")
    public Map<String, Object> attributes;
}


