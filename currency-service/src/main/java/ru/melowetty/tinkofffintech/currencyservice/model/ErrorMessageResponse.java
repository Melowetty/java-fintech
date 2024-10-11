package ru.melowetty.tinkofffintech.currencyservice.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorMessageResponse {
    public int status;
    public String message;
}
