CREATE SCHEMA IF NOT EXISTS payment_service;

CREATE TABLE IF NOT EXISTS payment_service.payments (
    id UUID PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL UNIQUE,
    order_id VARCHAR(64),
    user_id VARCHAR(64),
    merchant_id VARCHAR(64),
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    method VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payments_merchant ON payment_service.payments(merchant_id);
CREATE INDEX IF NOT EXISTS idx_payments_event_time ON payment_service.payments(event_time);


