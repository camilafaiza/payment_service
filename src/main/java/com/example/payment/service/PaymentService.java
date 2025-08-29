package com.example.payment.service;

import com.example.payment.domain.Payment;
import com.example.payment.dto.PaymentEvent;
import com.example.payment.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    @Transactional
    public Payment upsertFromEvent(PaymentEvent event) {
        Instant now = Instant.now();

        Optional<Payment> existing = paymentRepository.find("paymentId", event.paymentId).firstResultOptional();
        Payment p = existing.orElseGet(() -> {
            Payment np = new Payment();
            np.id = UUID.randomUUID();
            np.paymentId = event.paymentId;
            np.createdAt = now;
            return np;
        });

        p.orderId = event.orderId;
        p.userId = event.userId;
        p.merchantId = event.merchantId;
        p.amount = event.amount;
        p.currency = event.currency;
        p.method = event.method;
        p.status = event.status;
        p.eventTime = event.eventTime != null ? event.eventTime : now;
        p.updatedAt = now;

        if (existing.isEmpty()) {
            paymentRepository.persist(p);
        }

        return p;
    }
}


