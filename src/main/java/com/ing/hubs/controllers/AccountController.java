package com.ing.hubs.controllers;

import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.AccountBalanceDTO;
import com.ing.hubs.models.AccountDTO;
import com.ing.hubs.models.DepositRequest;
import com.ing.hubs.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<String> createAccount(@RequestBody AccountDTO accountDTO, HttpServletRequest request) throws UserNotFoundException, ConstraintException {

        accountService.createAccount(accountDTO, request);
        return ResponseEntity.ok("Successfully created account for currency:" + accountDTO.getCurrency());


    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest depositRequest, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, NotPositiveNumericException, HttpMessageNotReadableException {

        accountService.deposit(depositRequest.getCurrency(), depositRequest.getAmount(), request);
        return ResponseEntity.ok("You have successfully deposited: " + depositRequest.getAmount() + " " + depositRequest.getCurrency());

    }

    @GetMapping("/{accountId}")
    public ResponseEntity<String> showAccountBalance(@PathVariable(value = "accountId") int accountId, HttpServletRequest request) throws UserNotFoundException, UnauthorizedException, AccountNotFoundException {

        return ResponseEntity.ok("Your available balance for account no. " + accountId + " is " + accountService.getBalanceOne(accountId, request) + " " + accountService.findAccount(accountId).get().getCurrency());

    }

    @GetMapping()
    public ResponseEntity<String> showAllAccountsBalance(HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException {
        List<AccountBalanceDTO> accountList;
        accountList = accountService.getBalanceAll(request);
        String response = "Here are your current accounts:\n";
        for (AccountBalanceDTO account : accountList) {
            response = response + ("\nBalance for account in currency " + account.getCurrency() + " is " + account.getBalance());

        }
        System.out.println(response);
        return ResponseEntity.ok(response);

    }
}
