package com.ing.hubs.controllers;

import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.*;
import com.ing.hubs.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/app/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    public ResponseEntity<String> create(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, InsufficientFundsException, UnauthorizedException, ConstraintException, NotPositiveNumericException {

        Transaction transaction = transactionService.createTransaction(transactionRequest, request);
        String response = "Transaction completed successfully!\n";
        if (transaction.getType().equalsIgnoreCase("sendMoney")) {
            return ResponseEntity.ok("Transaction completed successfully!\nSuccessfully sent " + transaction.getAmount() + " " + transaction.getCurrency() + " from user " + transaction.getInitializerAccount().getUser().getFirstName() + " " + transaction.getInitializerAccount().getUser().getLastName() + " to user " + transaction.getTargetAccount().getUser().getFirstName() + " " + transaction.getTargetAccount().getUser().getLastName());
        } else if (transaction.getType().equalsIgnoreCase("requestMoney")) {
            return ResponseEntity.ok("Successfully received " + transaction.getAmount()
                    + " " + transaction.getCurrency()
                    + " from user " + transaction.getInitializerAccount().getUser().getFirstName() + " "
                    + transaction.getInitializerAccount().getUser().getLastName()
                    + " to user " + transaction.getTargetAccount().getUser().getFirstName()
                    + " " + transaction.getTargetAccount().getUser().getLastName()
            );
        }
        return ResponseEntity.ok("Transaction completed");


    }


    @GetMapping("/{accountId}")
    public ResponseEntity<String> showAccountHistory(@PathVariable(value = "accountId") int accountId, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, ConstraintException, UnauthorizedException, NoSuchElementException {
        List<Transaction> transactions;

        transactions = transactionService.getTransactionHistory(accountId, request);
        String response = "Here are your past transactions:\n";
        for (Transaction transaction : transactions) {
            if (transaction.getType().equalsIgnoreCase("sendMoney")) {
                response = response + ("\nIn " + transaction.getDate() + " you sent " + transaction.getAmount() + " " + transaction.getCurrency() + " to " + transaction.getTargetAccount().getUser().getFirstName() + " " + transaction.getTargetAccount().getUser().getLastName());
            } else if (transaction.getType().equalsIgnoreCase("requestMoney")) {
                response = response + ("\nIn " + transaction.getDate() + " you received " + transaction.getAmount() + " " + transaction.getCurrency() + " from " + transaction.getTargetAccount().getUser().getFirstName() + " " + transaction.getTargetAccount().getUser().getLastName());
            } else {
                response = response + ("\nIn " + transaction.getDate() + " you deposited " + transaction.getAmount() + " " + transaction.getCurrency() + " into your " + transaction.getCurrency() + " account");
            }
        }
        System.out.println(response);
        return ResponseEntity.ok(response);


    }

    @PostMapping("/exchange")
    public ResponseEntity<String> exchange(@RequestBody ExchangeRequest exchangeRequest, HttpServletRequest request) throws AccountNotFoundException, ConstraintException, UserNotFoundException, UnauthorizedException, NotPositiveNumericException {

        TransactionHistoryDTO transaction = transactionService.exchange(exchangeRequest.getInitializerAccountId(), exchangeRequest.getTargetAccountId(), exchangeRequest.getAmount(), request);
        if (transactionService.checkIfOwnerIsDifferent(exchangeRequest.getInitializerAccountId(), exchangeRequest.getTargetAccountId())) {
            return ResponseEntity.ok("Exchange completed successfully! You have exchanged " + exchangeRequest.getAmount() + " " + transaction.getInitializerAccount().getCurrency() + " from your " + transaction.getInitializerAccount().getCurrency() + " account to the " + transactionService.getAccount(exchangeRequest.getTargetAccountId()).get().getCurrency() + " account of " + transactionService.getAccount(exchangeRequest.getTargetAccountId()).get().getUser().getFirstName() + " " + transactionService.getAccount(exchangeRequest.getTargetAccountId()).get().getUser().getLastName());

        }

        return ResponseEntity.ok("Exchange completed successfully! You have exchanged " + exchangeRequest.getAmount() + " "
                + transaction.getInitializerAccount().getCurrency() + " from your " + transaction.getInitializerAccount().getCurrency() + " to your "
                + transaction.getTargetAccount().getCurrency() + " account");

    }


}