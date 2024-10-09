package ru.melowetty.tinkofffintech.currencyservice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.melowetty.tinkofffintech.currencyservice.exception.CentralBankServiceUnavailableException;
import ru.melowetty.tinkofffintech.currencyservice.exception.CurrencyNotFoundAtCentralBankException;
import ru.melowetty.tinkofffintech.currencyservice.model.BigErrorMessageResponse;
import ru.melowetty.tinkofffintech.currencyservice.model.ErrorMessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleCurrencyNotFoundAtCentralBank(CurrencyNotFoundAtCentralBankException e) {
        return new ErrorMessageResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageResponse> handleCentralBankServiceUnavailableException(CentralBankServiceUnavailableException e) {
        var body = new ErrorMessageResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage());
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER, "3600");
        return new ResponseEntity<>(body, headers, HttpStatus.SERVICE_UNAVAILABLE);
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
}
