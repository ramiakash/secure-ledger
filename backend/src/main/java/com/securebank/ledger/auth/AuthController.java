package com.securebank.ledger.auth;

import com.securebank.ledger.auth.dto.LoginRequest;
import com.securebank.ledger.auth.dto.LoginResponse;
import com.securebank.ledger.user.LedgerUserDetails;
import com.securebank.ledger.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Obtain a JWT for the transaction endpoints")
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

    @Operation(
            summary = "Authenticate and receive a JWT",
            description = "Verifies credentials and returns a signed bearer token. "
                    + "Failures return an identical 401 regardless of whether the "
                    + "account exists, to prevent username enumeration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Malformed request",
                    content = @Content)
    })
    @SecurityRequirements
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