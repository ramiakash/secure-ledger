package com.securebank.ledger.transaction.dto;

import com.securebank.ledger.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String description,
        String counterpartyIban,
        Instant bookedAt,
        Instant createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getDescription(),
                tx.getCounterpartyIban(),
                tx.getBookedAt(),
                tx.getCreatedAt());
    }
}