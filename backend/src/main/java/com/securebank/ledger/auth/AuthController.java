package com.securebank.ledger.auth;

import com.securebank.ledger.auth.dto.LoginRequest;
import com.securebank.ledger.auth.dto.LoginResponse;
import com.securebank.ledger.user.LedgerUserDetails;
import com.securebank.ledger.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        LedgerUserDetails principal = (LedgerUserDetails) authentication.getPrincipal();
        TokenService.IssuedToken token = tokenService.issue(principal);

        String displayName = userRepository.findById(principal.getId())
                .map(u -> u.getDisplayName())
                .orElse(principal.getUsername());

        return ResponseEntity.ok(new LoginResponse(
                token.value(),
                "Bearer",
                token.expiresInSeconds(),
                principal.getUsername(),
                displayName));
    }
}