package com.ing.hubs.services;

import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.Account;
import com.ing.hubs.models.AccountBalanceDTO;
import com.ing.hubs.models.AccountDTO;
import com.ing.hubs.models.User;
import com.ing.hubs.repositories.AccountRepository;
import com.ing.hubs.repositories.UserRepository;
import com.ing.hubs.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public void createAccount(AccountDTO accountDTO, HttpServletRequest request) throws UserNotFoundException, ConstraintException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Optional<Account> existingAccount = accountRepository.findByCurrencyAndUserId(accountDTO.getCurrency(), user.getId());
        if (existingAccount.isPresent()) {
            throw new ConstraintException("You already have an account with " + accountDTO.getCurrency() + " currency! Choose another currency");
        }

        Account account = Account.builder()
                .currency(accountDTO.getCurrency())
                .balance(0.0)
                .user(user)
                .build();
        accountRepository.save(account);

    }

    @Transactional
    public void updateBalance(Account account, double amount) {

        account.setBalance(account.getBalance()+amount);
        accountRepository.save(account);

    }

    public double getBalanceOne(int id, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, UnauthorizedException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Account account = accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);

        if (account.getUser().getId() == user.getId()) {

            return account.getBalance();
        } else {
            throw new UnauthorizedException("You are not authorized to view balance for this account because account is not yours!");
        }
    }

    public List<AccountBalanceDTO> getBalanceAll(HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        List<AccountBalanceDTO> accounts = new ArrayList<>();
        List<Account> allAccounts = accountRepository.findAllByUserId(user.getId());
        if (allAccounts.isEmpty()) {
            throw new AccountNotFoundException("You have no accounts!");
        }
        for (Account account : allAccounts) {
            AccountBalanceDTO accountBalanceDTO = AccountBalanceDTO.builder()
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .build();
            accounts.add(accountBalanceDTO);
        }
        return accounts;
    }


    @Transactional
    public void deposit(String currency, Double amount, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, NotPositiveNumericException {
        if (amount < 0) {
            throw new NotPositiveNumericException();
        }
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        List<Account> accounts = accountRepository.findByCurrencyAndUserId(currency, user.getId()).stream().toList();
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("Could not find account with currency " + currency + "! Make sure you have an account in that currency!");
        }

        for (Account account : accounts) {
            if (account.getCurrency().equals(currency)) {
                updateBalance(account,amount);
                accountRepository.save(account);

            }
        }

    }

    public boolean checkIfOwnerDifferent(int initializerAccountId, int targetAccountId){
        if(findAccount(initializerAccountId).get().getId() != findAccount(targetAccountId).get().getId()){
            System.out.println(findAccount(initializerAccountId).get().getId());
            System.out.println(findAccount(targetAccountId).get().getId());

            return true;
        }
        return false;
    }

    public Optional<Account> findAccount(Integer id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccountsOfUser(Integer userId) {
        return accountRepository.findAllByUserId(userId);
    }
}
