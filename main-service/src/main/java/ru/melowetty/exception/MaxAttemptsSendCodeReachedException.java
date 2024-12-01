package ru.melowetty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MaxAttemptsSendCodeReachedException extends RuntimeException {
    public MaxAttemptsSendCodeReachedException(String message) {
        super(message);
    }
}