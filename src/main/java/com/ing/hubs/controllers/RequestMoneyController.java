package com.ing.hubs.controllers;

import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.Request;
import com.ing.hubs.models.Transaction;
import com.ing.hubs.models.TransactionRequest;
import com.ing.hubs.services.RequestMoneyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/requests")
@RequiredArgsConstructor
public class RequestMoneyController {
    private final RequestMoneyService requestMoneyService;

    @PostMapping()
    public ResponseEntity<String> create(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) throws UserNotFoundException, UnauthorizedException, AccountNotFoundException {
        Request moneyRequest = requestMoneyService.createMoneyRequest(transactionRequest, request);
        return ResponseEntity.ok("Successfully created a request for " + moneyRequest.getAmount()
                + " " + requestMoneyService.getAccount(transactionRequest.getInitializerAccountId()).getCurrency()
                + " from user " + moneyRequest.getSenderAccount().getUser().getFirstName() + " "
                + moneyRequest.getSenderAccount().getUser().getLastName()
                + ". Please wait for his/hers approval before transaction executes"
        );

    }

    @GetMapping()
    public ResponseEntity<String> view(HttpServletRequest request) throws UserNotFoundException, UnauthorizedException, AccountNotFoundException {
        List<Request> moneyRequests;

        moneyRequests = requestMoneyService.viewPendingRequests(request);
        String response = "Here are your pending requests:\n";
        if (moneyRequests.isEmpty()) {
            return ResponseEntity.ok("You have no pending requests");
        }

        for (Request moneyRequest : moneyRequests) {
            response = response + ("\n" + moneyRequest.getId() + " User " + moneyRequest.getReceiverAccount().getUser().getFirstName() + " " + moneyRequest.getReceiverAccount().getUser().getLastName() + " requested " + moneyRequest.getAmount() + " " + moneyRequest.getReceiverAccount().getCurrency());
        }
        return ResponseEntity.ok(response);

    }

    @PostMapping("/{requestId}")
    public ResponseEntity<String> approveRequest(@PathVariable(value = "requestId") int requestId, HttpServletRequest request) throws UserNotFoundException, UnauthorizedException, RequestNotFoundException, AccountNotFoundException, InsufficientFundsException, ConstraintException, NotPositiveNumericException {

        Request moneyRequest = requestMoneyService.approveRequest(requestId, request);
        return ResponseEntity.ok("Successfully approved request and sent " + moneyRequest.getAmount() + " " + moneyRequest.getReceiverAccount().getCurrency() + " to " + moneyRequest.getReceiverAccount().getUser().getFirstName() + " " + moneyRequest.getReceiverAccount().getUser().getLastName());

    }

}
