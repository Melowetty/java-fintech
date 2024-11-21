package ru.melowetty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.melowetty.exception.ChangePasswordTokenIsExpired;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.exception.MaxAttemptsSendCodeReachedException;
import ru.melowetty.exception.RelatedEntityNotFoundException;
import ru.melowetty.exception.UsernameAlreadyExists;
import ru.melowetty.exception.WrongAuthCodeException;
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
    public ErrorMessageResponse handleChangePasswordTokenIsExpired(ChangePasswordTokenIsExpired e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleWrongAuthCode(WrongAuthCodeException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleMaxAttemptsSendCodeReached(MaxAttemptsSendCodeReachedException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ErrorMessageResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return new ErrorMessageResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleRelatedEntityNotFoundException(RelatedEntityNotFoundException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessageResponse handleBadCredentials(BadCredentialsException e) {
        return new ErrorMessageResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
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
