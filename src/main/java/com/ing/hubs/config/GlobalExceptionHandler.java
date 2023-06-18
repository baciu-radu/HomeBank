package com.ing.hubs.config;

import com.ing.hubs.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.NoSuchElementException;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundException(UserNotFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {AccountNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage accountNotFoundException(AccountNotFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {RequestNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage requestNotFoundException(RequestNotFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedException(UnauthorizedException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {ConstraintException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage constraintException(ConstraintException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {InsufficientFundsException.class})
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage insufficientFundsException(InsufficientFundsException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {NotPositiveNumericException.class})
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage notPositiveNumericException(NotPositiveNumericException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .timestamp(new Date())
                .message(ex.getMessage())
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage incorrectInputDataType(HttpMessageNotReadableException ex) {
        String customErrorMessage;
        if(ex.getMessage().contains("Double")){
            customErrorMessage= "Incorrect Data Type for amount. Please enter only numbers!";
        } else {
            customErrorMessage = "Incorrect Data Type for account. Please use only numbers as an ID!";
        }
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .timestamp(new Date())
                .message(customErrorMessage)
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage incorrectInputDataType(NoSuchElementException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .message("Could not find account! Please try again")
                .build();
        return errorMessage;
    }

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage incorrectInputDataType(SQLIntegrityConstraintViolationException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(new Date())
                .message("User already exists. Chose another username")
                .build();
        return errorMessage;
    }
}
