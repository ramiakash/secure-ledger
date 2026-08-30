package com.securebank.ledger.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Iban {

    String message() default "must be a valid IBAN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}