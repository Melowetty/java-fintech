package ru.melowetty.tinkofffintech.currencyservice.exception;

import java.util.Currency;

public class CurrencyNotFoundAtCentralBankException extends RuntimeException {
    public CurrencyNotFoundAtCentralBankException(Currency currency) {
        super(String.format("Валюта %s не найдена в центробанке", currency.getCurrencyCode()));
    }
}
