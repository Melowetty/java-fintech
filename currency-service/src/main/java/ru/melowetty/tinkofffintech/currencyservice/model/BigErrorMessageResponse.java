package ru.melowetty.tinkofffintech.currencyservice.model;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class BigErrorMessageResponse {
    public int status;
    public List<String> errors;
}

