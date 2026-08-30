package com.securebank.ledger.transaction;

import com.securebank.ledger.common.AuthenticatedUser;
import com.securebank.ledger.transaction.dto.CreateTransactionRequest;
import com.securebank.ledger.transaction.dto.PageResponse;
import com.securebank.ledger.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }



    private static final Set<String> SORTABLE = Set.of("bookedAt", "amount", "createdAt");
    private static final int MAX_PAGE_SIZE = 100;

    @GetMapping
    public PageResponse<TransactionResponse> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "bookedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        UUID ownerId = AuthenticatedUser.currentUserId();

        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!SORTABLE.contains(sort)) {
            throw new IllegalArgumentException("sort must be one of " + SORTABLE);
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }

        Sort.Direction dir = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(dir, sort).and(Sort.by(Sort.Direction.DESC, "id")));

        Instant fromInstant = from != null
                ? from.atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.EPOCH;
        Instant toExclusive = to != null
                ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.now().plusSeconds(86_400);

        return PageResponse.of(
                transactionService.findPage(ownerId, fromInstant, toExclusive, pageable),
                TransactionResponse::from);
    }
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
           @Valid @RequestBody CreateTransactionRequest request) {

        UUID ownerId = AuthenticatedUser.currentUserId();
        Transaction saved = transactionService.record(ownerId, request);

        return ResponseEntity
                .created(URI.create("/api/v1/transactions/" + saved.getId()))
                .body(TransactionResponse.from(saved));
    }
}