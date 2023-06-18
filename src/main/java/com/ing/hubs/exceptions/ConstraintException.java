package com.ing.hubs.exceptions;


public class ConstraintException extends Exception {
    public ConstraintException(String message) {
        super(message);
    }
    public ConstraintException() {
        super("You already have an account in that currency!");
    }
}
