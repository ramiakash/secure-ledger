package com.securebank.ledger.transaction;

import com.securebank.ledger.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {


    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "counterparty_iban", nullable = false, length = 34)
    private String counterpartyIban;

    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected Transaction() {
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getCounterpartyIban() { return counterpartyIban; }
    public Instant getBookedAt() { return bookedAt; }
    public Instant getCreatedAt() { return createdAt; }


    public static Transaction record(User owner,
                                     BigDecimal amount,
                                     String currency,
                                     String description,
                                     String counterpartyIban,
                                     Instant bookedAt) {
        Transaction tx = new Transaction();
        tx.id = UUID.randomUUID();
        tx.user = owner;
        tx.amount = amount;
        tx.currency = currency.toUpperCase();
        tx.description = description.trim();
        tx.counterpartyIban = counterpartyIban.replace(" ", "").toUpperCase();
        tx.bookedAt = bookedAt;
        tx.createdAt = Instant.now();
        return tx;
    }
}
