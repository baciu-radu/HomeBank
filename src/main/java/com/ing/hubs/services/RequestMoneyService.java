package com.ing.hubs.services;

import com.ing.hubs.exceptions.*;
import com.ing.hubs.models.*;
import com.ing.hubs.repositories.RequestRepository;
import com.ing.hubs.repositories.UserRepository;
import com.ing.hubs.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestMoneyService {
    private final JwtService jwtService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;


    @Transactional
    public Request createMoneyRequest(TransactionRequest transactionRequest, HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, UnauthorizedException, HttpMessageNotReadableException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Account initializerAccount = accountService.findAccount(transactionRequest.getInitializerAccountId()).orElseThrow(AccountNotFoundException::new);
        Account targetAccount = accountService.findAccount(transactionRequest.getTargetAccountId()).orElseThrow(AccountNotFoundException::new);
        Request moneyRequest;
        if (user.getUsername().equals(initializerAccount.getUser().getUsername())) {
            moneyRequest = Request.builder()
                    .requestStatus(RequestStatus.PENDING)
                    .amount(transactionRequest.getAmount())
                    .senderAccount(targetAccount)
                    .receiverAccount(initializerAccount)
                    .build();
            requestRepository.save(moneyRequest);
            return moneyRequest;
        } else {

            throw new UnauthorizedException("You are not authorized to create a money request for an account that is not yours! Account no " + transactionRequest.getInitializerAccountId() + " is not yours!");
        }

    }

    @Transactional
    public List<Request> viewPendingRequests(HttpServletRequest request) throws UserNotFoundException, AccountNotFoundException, UnauthorizedException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(request));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        List<Account> accounts = accountService.getAllAccountsOfUser(user.getId());
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException();
        }
        if (user.getUsername().equals(accounts.get(0).getUser().getUsername())) {
            List<Request> requests = new ArrayList<>();
            for (Account account : accounts) {
                for (Request moneyRequest : requestRepository.findAllBySenderAccountAndRequestStatus(account, RequestStatus.PENDING))
                    requests.add(moneyRequest);
            }
            return requests;
        } else {

            throw new UnauthorizedException("Unauthorized access! You can only view requests for your own accounts!");
        }

    }

    @Transactional
    public Request approveRequest(Integer requestId, HttpServletRequest httpServletRequest) throws UserNotFoundException, RequestNotFoundException, AccountNotFoundException, UnauthorizedException, InsufficientFundsException, ConstraintException, NotPositiveNumericException {
        String username = jwtService.extractUsername(jwtService.extractJwtFromRequest(httpServletRequest));
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        if (user.getUsername().equals(request.getSenderAccount().getUser().getUsername())) {
            request.setRequestStatus(RequestStatus.APPROVED);
            requestRepository.save(request);
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .amount(request.getAmount())
                    .type("sendMoney")
                    .initializerAccountId(request.getSenderAccount().getId())
                    .targetAccountId(request.getReceiverAccount().getId())
                    .build();
            transactionService.createTransaction(transactionRequest, httpServletRequest);
            return request;
        }else {

            throw new UnauthorizedException("Unauthorized access! You can only view requests for your own accounts!");
        }
    }
    public Account getAccount(int id){
        return accountService.findAccount(id).get();
    }
}
