package com.radomskyi.budgeter.exception;

public class InvestmentTransactionNotFoundException extends RuntimeException {

    public InvestmentTransactionNotFoundException(String message) {
        super(message);
    }

    public InvestmentTransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
