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

    @Scheduled(every = "10s")
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
        e.attributes = Map.of("bank", "BCA", "channel", RANDOM.nextBoolean() ? "web" : "app");

        paymentEmitter.send(objectMapper.writeValueAsString(e));
    }
}


