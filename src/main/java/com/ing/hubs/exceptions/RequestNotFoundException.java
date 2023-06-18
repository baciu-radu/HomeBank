package com.ing.hubs.exceptions;


public class RequestNotFoundException extends Exception {
    public RequestNotFoundException() {
        super("Request not found! Please check request id");
    }
}
