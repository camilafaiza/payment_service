package com.example.payment.messaging;

import com.example.payment.dto.PaymentEvent;
import com.example.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PaymentMessaging {

    @Inject
    PaymentService paymentService;

    @Inject
    ObjectMapper objectMapper;

    @Channel("payment-transformed")
    Emitter<String> transformedEmitter;

    @Incoming("payment-events")
    @Blocking
    public void onPaymentEvent(String payload) throws Exception {
        PaymentEvent event = objectMapper.readValue(payload, PaymentEvent.class);

        // Manipulasi/validasi sederhana (contoh): normalisasi currency uppercase
        if (event.currency != null) {
            event.currency = event.currency.toUpperCase();
        }

        // Trim ID fields untuk menghindari spasi tak sengaja
        if (event.paymentId != null) event.paymentId = event.paymentId.trim();
        if (event.orderId != null) event.orderId = event.orderId.trim();
        if (event.userId != null) event.userId = event.userId.trim();
        if (event.merchantId != null) event.merchantId = event.merchantId.trim();

        // Pastikan amount tidak negatif
        if (event.amount < 0) {
            event.amount = 0;
        }

        // Normalisasi method ke daftar yang diizinkan
        if (event.method != null) {
            String m = event.method.toLowerCase();
            switch (m) {
                case "card":
                case "ewallet":
                case "va":
                case "qris":
                    event.method = m;
                    break;
                default:
                    event.method = "other";
            }
        } else {
            event.method = "other";
        }

        // Normalisasi status ke daftar yang diizinkan
        if (event.status != null) {
            String s = event.status.toLowerCase();
            switch (s) {
                case "authorized":
                case "captured":
                case "failed":
                case "refunded":
                    event.status = s;
                    break;
                default:
                    event.status = "unknown";
            }
        } else {
            event.status = "unknown";
        }

        // Enrichment: tambahkan atribut turunan
        Map<String, Object> attrs = new HashMap<>();
        if (event.attributes != null) {
            attrs.putAll(event.attributes);
        }
        attrs.put("processedAt", Instant.now().toString());
        attrs.put("highValue", event.amount >= 50_000);
        if ("IDR".equals(event.currency)) {
            attrs.put("currencySymbol", "Rp");
        }
        event.attributes = attrs;

        // Persist/update state ke database
        paymentService.upsertFromEvent(event);

        // Emit ke topik enriched/transformed
        String enriched = serialize(event);
        transformedEmitter.send(enriched);
    }

    private String serialize(PaymentEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }
}


