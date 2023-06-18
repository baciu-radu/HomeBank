package com.ing.hubs.exceptions;


public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(){super("Your are not authorized for this operation! Make sure you are the owner of the account!");}
}
