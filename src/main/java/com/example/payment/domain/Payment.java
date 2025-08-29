package com.example.payment.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "payment_id", nullable = false, unique = true)
    public String paymentId;

    @Column(name = "order_id")
    public String orderId;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "merchant_id")
    public String merchantId;

    @Column(name = "amount", nullable = false)
    public long amount;

    @Column(name = "currency", length = 3, nullable = false)
    public String currency;

    @Column(name = "method", nullable = false)
    public String method;

    @Column(name = "status", nullable = false)
    public String status;

    @Column(name = "event_time", nullable = false)
    public Instant eventTime;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}


