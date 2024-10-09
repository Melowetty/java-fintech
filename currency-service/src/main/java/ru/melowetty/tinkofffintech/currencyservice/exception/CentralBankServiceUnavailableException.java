package ru.melowetty.tinkofffintech.currencyservice.exception;

public class CentralBankServiceUnavailableException extends RuntimeException {
    public CentralBankServiceUnavailableException() {
        super("Сервис центрального банка недоступен, попробуйте выполнить запрос через час");
    }
}
