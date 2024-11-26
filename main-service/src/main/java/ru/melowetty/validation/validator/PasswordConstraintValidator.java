package ru.melowetty.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.melowetty.validation.annotation.ValidPassword;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String SPECIAL_CHARS = "!@#$%^&*()_+";
    private static final int MIN_LENGTH = 8;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean hasDigits = false;
        boolean hasLetters = false;
        boolean hasSpecialChars = false;

        if (value.length() < MIN_LENGTH) {
            return false;
        }

        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) hasDigits = true;
            else if (Character.isLetter(c)) hasLetters = true;
            else if (SPECIAL_CHARS.indexOf(c) != -1) hasSpecialChars = true;
        }

        return hasDigits && hasLetters && hasSpecialChars;
    }
}
