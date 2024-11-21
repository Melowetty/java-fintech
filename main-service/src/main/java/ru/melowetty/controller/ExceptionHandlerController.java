package ru.melowetty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.melowetty.exception.UsernameAlreadyExists;
import ru.melowetty.model.BigErrorMessageResponse;
import ru.melowetty.model.ErrorMessageResponse;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleUsernameAlreadyExists(UsernameAlreadyExists e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BigErrorMessageResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return new BigErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageResponse handleRuntimeException(RuntimeException e) {
        return new ErrorMessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}
