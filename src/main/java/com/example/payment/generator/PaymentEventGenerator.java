package com.example.payment.generator;

import com.example.payment.dto.PaymentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class PaymentEventGenerator {

    private static final String[] METHODS = new String[]{"card", "ewallet", "va", "qris"};
    private static final String[] STATUSES = new String[]{"authorized", "captured", "failed", "refunded"};
    private static final Random RANDOM = new Random();

    @Inject
    ObjectMapper objectMapper;

    @Channel("payment-events-out")
    Emitter<String> paymentEmitter;

    @Scheduled(every = "10s", delayed = "15s")
    void tick() throws JsonProcessingException {
        PaymentEvent e = new PaymentEvent();
        e.eventId = UUID.randomUUID().toString();
        e.eventType = "payment_authorized";
        e.eventTime = Instant.now();
        e.paymentId = "pay_" + UUID.randomUUID();
        e.orderId = "ord_" + RANDOM.nextInt(10000);
        e.userId = "usr_" + RANDOM.nextInt(1000);
        e.merchantId = "mrc_" + (1 + RANDOM.nextInt(5));
        e.amount = 10000 + RANDOM.nextInt(90000);
        e.currency = "IDR";
        e.method = METHODS[RANDOM.nextInt(METHODS.length)];
        e.status = STATUSES[RANDOM.nextInt(STATUSES.length)];
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("bank", "BCA");
        attrs.put("channel", RANDOM.nextBoolean() ? "web" : "app");

        // Perkenalkan variasi/anomali agar consumer dapat menormalisasi
        // 30%: currency jadi huruf kecil
        if (RANDOM.nextDouble() < 0.30) {
            e.currency = e.currency.toLowerCase(); // "idr"
        }
        // 20%: amount negatif
        if (RANDOM.nextDouble() < 0.20) {
            e.amount = -e.amount;
        }
        // 30%: tambahkan spasi di ID fields
        if (RANDOM.nextDouble() < 0.30) e.paymentId = " " + e.paymentId + " ";
        if (RANDOM.nextDouble() < 0.30) e.orderId = "  " + e.orderId + "  ";
        if (RANDOM.nextDouble() < 0.30) e.userId = "\t" + e.userId + "\t";
        if (RANDOM.nextDouble() < 0.30) e.merchantId = e.merchantId + "  ";
        // 25%: method tak dikenal / case aneh
        if (RANDOM.nextDouble() < 0.25) {
            e.method = RANDOM.nextBoolean() ? "Card" : "bank_transfer";
        }
        // 25%: status tak dikenal / case aneh
        if (RANDOM.nextDouble() < 0.25) {
            e.status = RANDOM.nextBoolean() ? "Completed" : "DONE";
        }
        // 15%: eventTime null (akan diisi di service)
        if (RANDOM.nextDouble() < 0.15) {
            e.eventTime = null;
        }
        // 20%: atribut dengan spasi/variasi casing
        if (RANDOM.nextDouble() < 0.20) {
            attrs.put("bank", "  bca  ");
            attrs.put("Channel", "WEB");
        }
        e.attributes = attrs;

        paymentEmitter.send(objectMapper.writeValueAsString(e));
    }
}


