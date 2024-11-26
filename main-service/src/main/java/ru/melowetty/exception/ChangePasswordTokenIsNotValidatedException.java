package ru.melowetty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ChangePasswordTokenIsNotValidatedException extends RuntimeException {
    public ChangePasswordTokenIsNotValidatedException(String message) {
        super(message);
    }
}
