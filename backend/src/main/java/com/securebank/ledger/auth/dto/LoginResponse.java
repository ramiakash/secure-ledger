package com.securebank.ledger.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        String displayName
) {}