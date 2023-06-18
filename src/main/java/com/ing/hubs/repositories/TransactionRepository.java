package com.ing.hubs.repositories;

import com.ing.hubs.models.Account;
import com.ing.hubs.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findAllByInitializerAccount(Account account);

    List<Transaction> findAllByTargetAccount(Account account);
}