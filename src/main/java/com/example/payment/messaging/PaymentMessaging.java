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
import org.eclipse.microprofile.reactive.messaging.Outgoing;

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


