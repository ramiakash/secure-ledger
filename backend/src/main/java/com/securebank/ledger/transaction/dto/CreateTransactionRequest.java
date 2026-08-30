package com.securebank.ledger.transaction.dto;

import com.securebank.ledger.validation.Iban;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateTransactionRequest(

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "amount must be positive")
        @Digits(integer = 15, fraction = 4,
                message = "amount must have at most 15 integer and 4 decimal digits")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "currency must be a 3-letter code")
        String currency,

        @NotBlank(message = "description is required")
        @Size(max = 255, message = "description must not exceed 255 characters")
        String description,

        @NotBlank(message = "counterpartyIban is required")
        @Iban
        String counterpartyIban
) {}