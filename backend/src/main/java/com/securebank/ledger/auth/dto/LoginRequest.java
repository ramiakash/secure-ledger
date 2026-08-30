package com.securebank.ledger.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "username is required")
        @Size(max = 64)
        String username,

        @NotBlank(message = "password is required")
        @Size(max = 128)
        String password
) {
    @Override
    public String toString() {
        return "LoginRequest[username=" + username + ", password=***]";
    }
}