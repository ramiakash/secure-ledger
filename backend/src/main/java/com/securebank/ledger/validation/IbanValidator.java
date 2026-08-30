package com.securebank.ledger.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class IbanValidator implements ConstraintValidator<Iban, String> {

    private static final Pattern STRUCTURE =
            Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String normalised = value.replace(" ", "").toUpperCase();

        if (!STRUCTURE.matcher(normalised).matches()) {
            return false;
        }
        return mod97(normalised) == 1;
    }

    private int mod97(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numeric.append(c);
            } else {
                numeric.append(Character.getNumericValue(c));
            }
        }

        return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue();
    }
}