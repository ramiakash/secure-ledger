package com.securebank.ledger.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT in the security context");
        }

        String uid = jwt.getClaimAsString("uid");
        if (uid == null) {
            throw new IllegalStateException("Token is missing the required 'uid' claim");
        }
        return UUID.fromString(uid);
    }
}