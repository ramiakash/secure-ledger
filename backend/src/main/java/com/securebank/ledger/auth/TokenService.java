package com.securebank.ledger.auth;

import com.securebank.ledger.config.JwtProperties;
import com.securebank.ledger.user.LedgerUserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public TokenService(JwtEncoder jwtEncoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public IssuedToken issue(LedgerUserDetails user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUsername())
                .claim("uid", user.getId().toString())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        return new IssuedToken(value, properties.accessTokenTtl().toSeconds());
    }

    public record IssuedToken(String value, long expiresInSeconds) {
    }
}