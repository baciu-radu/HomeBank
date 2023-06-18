package com.ing.hubs.exceptions;

public class NotPositiveNumericException extends Exception {
    public NotPositiveNumericException() {
        super("Please provide a positive number.");
    }
}

