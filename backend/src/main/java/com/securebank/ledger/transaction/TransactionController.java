package com.securebank.ledger.transaction;

import com.securebank.ledger.transaction.dto.CreateTransactionRequest;
import com.securebank.ledger.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // TEMPORARY: the owner is hardcoded until security exists in Step 11.
    private static final UUID TEMP_USER =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @GetMapping
    public List<TransactionResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "bookedAt"));

        return transactionService
                .findPage(TEMP_USER, Instant.EPOCH, Instant.now().plusSeconds(86400), pageable)
                .map(TransactionResponse::from)
                .getContent();
    }
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
           @Valid @RequestBody CreateTransactionRequest request) {

        Transaction saved = transactionService.record(TEMP_USER, request);

        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + saved.getId()))
                .body(TransactionResponse.from(saved));
    }
}