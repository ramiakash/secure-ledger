package com.securebank.ledger.error;

import com.securebank.ledger.error.ApiExceptions.InvalidQueryException;
import com.securebank.ledger.error.ApiExceptions.TransactionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail onValidationFailure(MethodArgumentNotValidException ex,
                                             HttpServletRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::describe)
                .toList();

        ProblemDetail problem = base(HttpStatus.UNPROCESSABLE_ENTITY,
                "Validation failed",
                "One or more fields failed validation. See 'errors' for details.",
                request);
        problem.setProperty("errors", errors);
        return problem;
    }

    private Map<String, String> describe(FieldError error) {
        Map<String, String> detail = new LinkedHashMap<>();
        detail.put("field", error.getField());
        detail.put("message", error.getDefaultMessage());
        return detail;
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ProblemDetail onInvalidQuery(InvalidQueryException ex, HttpServletRequest request) {
        return base(HttpStatus.BAD_REQUEST, "Invalid query", ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail onMalformedBody(HttpMessageNotReadableException ex,
                                         HttpServletRequest request) {
        log.debug("Malformed request body on {}", request.getRequestURI(), ex);
        return base(HttpStatus.BAD_REQUEST, "Malformed request",
                "The request body could not be parsed as valid JSON.", request);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ProblemDetail onBadCredentials(Exception ex, HttpServletRequest request) {
        log.info("Failed authentication attempt from {}", request.getRemoteAddr());
        return base(HttpStatus.UNAUTHORIZED, "Authentication failed",
                "Invalid username or password.", request);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ProblemDetail onNotFound(TransactionNotFoundException ex,
                                    HttpServletRequest request) {
        return base(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onUnexpected(Exception ex, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Unhandled exception [correlationId={}] on {} {}",
                correlationId, request.getMethod(), request.getRequestURI(), ex);

        ProblemDetail problem = base(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred. Quote the correlation id when reporting this.",
                request);
        problem.setProperty("correlationId", correlationId);
        return problem;
    }

    private ProblemDetail base(HttpStatus status, String title, String detail,
                               HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}