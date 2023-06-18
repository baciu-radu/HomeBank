package com.ing.hubs.exceptions;


public class AccountNotFoundException extends Exception {
    public AccountNotFoundException() {
        super("Account not found! Please try again.");
    }
    public AccountNotFoundException(String message) {
        super(message);
    }
}
