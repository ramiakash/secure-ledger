package com.securebank.ledger.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUserIdAndBookedAtGreaterThanEqualAndBookedAtLessThan(
            UUID userId, Instant from, Instant toExclusive, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
}