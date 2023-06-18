package com.ing.hubs.services;


import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.*;
import com.ing.hubs.repositories.ExchangeRateRepository;
import com.ing.hubs.repositories.TransactionRepository;
import com.ing.hubs.repositories.UserRepository;
import com.ing.hubs.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    @Autowired
    private AccountService accountService;

    @Transactional
    public Transaction createTransaction(TransactionRequest transactionRequest, HttpServletRequest request) throws AccountNotFoundException, NotPositiveNumericException, UserNotFoundException, UnauthorizedException, InsufficientFundsException, ConstraintException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (transactionRequest.getAmount() < 0) {
            throw new NotPositiveNumericException();
        }
        Account initializerAccount = accountService.findAccount(transactionRequest.getInitializerAccountId()).orElseThrow(AccountNotFoundException::new);
        Account targetAccount = accountService.findAccount(transactionRequest.getTargetAccountId()).orElseThrow(AccountNotFoundException::new);
        if (initializerAccount.getUser().getUsername() != user.getUsername()) {
            throw new UnauthorizedException("You are not authorized to make transactions from this account because account is not yours!");
        }

        if (initializerAccount.getBalance() < transactionRequest.getAmount()) {
            throw new InsufficientFundsException("You do not have enough money in your " + initializerAccount.getCurrency() + "account! Could not complete transaction");
        }

        Transaction transaction = Transaction.builder()
                .amount(transactionRequest.getAmount())
                .currency(initializerAccount.getCurrency())
                .type(transactionRequest.getType())
                .date(LocalDate.now())
                .initializerAccount(initializerAccount)
                .targetAccount(targetAccount)
                .build();
        transactionRepository.save(transaction);
        if (transactionRequest.getType().equalsIgnoreCase("sendMoney")) {

            accountService.updateBalance(initializerAccount, 0 - transaction.getAmount());
            accountService.updateBalance(targetAccount, transaction.getAmount());
            System.out.println("Successfully transferred " + transaction.getAmount()
                    + " " + transaction.getCurrency()
                    + " from user " + initializerAccount.getUser().getFirstName() + " "
                    + initializerAccount.getUser().getLastName()
                    + " to user " + targetAccount.getUser().getFirstName()
                    + " " + targetAccount.getUser().getLastName()
            );

        } else throw new ConstraintException("Transaction type incorrect!");

        return transaction;
    }

    public List<Transaction> getTransactionHistory(Integer accountId, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, ConstraintException, UnauthorizedException {

        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        if (accountService.findAccount(accountId).get().getUser().getId() != user.getId()) {

            throw new UnauthorizedException("You are not authorized to view transaction history for this account because account is not yours!");
        }
        Account account = accountService.findAccount(accountId).orElseThrow(AccountNotFoundException::new);

        List<Transaction> transactions;
        var targetAccounts = transactionRepository.findAllByTargetAccount(account);
        var initializerAccounts = transactionRepository.findAllByInitializerAccount(account);
        transactions = Stream.concat(targetAccounts.stream(), initializerAccounts.stream())
                .collect(Collectors.toList());
        if (transactions.isEmpty()) {
            throw new ConstraintException("You do not have any transactions for that account");
        }

        return Stream.concat(targetAccounts.stream(), initializerAccounts.stream())
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionHistoryDTO exchange(Integer initializerAccountId, Integer targetAccountId, double amount, HttpServletRequest request) throws AccountNotFoundException, NotPositiveNumericException, ConstraintException, UserNotFoundException, UnauthorizedException, HttpMessageNotReadableException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (accountService.findAccount(initializerAccountId).get().getUser().getUsername() != user.getUsername()) {
            throw new UnauthorizedException("You are not authorized to make transactions from this account because account is not yours!");
        }

        if (amount < 0) {
            throw new NotPositiveNumericException();
        }
        Account initializerAccount = accountService.findAccount(initializerAccountId).orElseThrow(AccountNotFoundException::new);
        Account targetAccount = accountService.findAccount(targetAccountId).orElseThrow(AccountNotFoundException::new);

        ExchangeRate exchangeRate;
        try {
            exchangeRate = exchangeRateRepository.findByInputCurrencyAndOutputCurrency(initializerAccount.getCurrency(), targetAccount.getCurrency()).get();
        } catch (NoSuchElementException ex) {
            throw new ConstraintException("Could not find exchange pair for " + initializerAccount.getCurrency() + " - " + targetAccount.getCurrency() + " !");
        }

        Transaction transaction = Transaction.builder()
                .amount(amount * exchangeRate.getRate())
                .currency(targetAccount.getCurrency())
                .type("Exchange")
                .date(LocalDate.now())
                .initializerAccount(initializerAccount)
                .targetAccount(targetAccount)
                .build();
        TransactionHistoryDTO transactionHistoryDTO = TransactionHistoryDTO.builder()
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .date(transaction.getDate())
                .currency(transaction.getCurrency())
                .initializerAccount(transaction.getInitializerAccount())
                .targetAccount(transaction.getTargetAccount())
                .build();

        transactionRepository.save(transaction);
        accountService.updateBalance(initializerAccount, 0 - amount);
        accountService.updateBalance(targetAccount, amount * exchangeRate.getRate());
        return transactionHistoryDTO;
    }

    public boolean checkIfOwnerIsDifferent(int initializerAccountId, int targetAccountId) {
        return accountService.checkIfOwnerDifferent(initializerAccountId, targetAccountId);
    }

    public Optional<Account> getAccount(int accountId) {

        return accountService.findAccount(accountId);

    }
}
