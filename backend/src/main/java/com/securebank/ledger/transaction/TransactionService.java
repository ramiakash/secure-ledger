package com.securebank.ledger.transaction;

import com.securebank.ledger.transaction.dto.CreateTransactionRequest;
import com.securebank.ledger.user.User;
import com.securebank.ledger.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findPage(UUID ownerId, Instant from, Instant toExclusive,
                                      Pageable pageable) {
        return transactionRepository
                .findByUserIdAndBookedAtGreaterThanEqualAndBookedAtLessThan(
                        ownerId, from, toExclusive, pageable);
    }
    @Transactional
    public Transaction record(UUID ownerId, CreateTransactionRequest request) {
        User owner = userRepository.getReferenceById(ownerId);

        Transaction transaction = Transaction.record(
                owner,
                request.amount(),
                request.currency(),
                request.description(),
                request.counterpartyIban(),
                Instant.now());

        return transactionRepository.save(transaction);
    }

}