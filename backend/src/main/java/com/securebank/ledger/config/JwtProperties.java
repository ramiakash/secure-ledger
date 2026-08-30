package com.securebank.ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ledger.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Duration accessTokenTtl
) {}