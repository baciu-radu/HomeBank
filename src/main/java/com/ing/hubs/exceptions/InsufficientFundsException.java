package com.ing.hubs.exceptions;


public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds! Please enter another amount");
    }
    public InsufficientFundsException(String message) {
        super(message);
    }
}
