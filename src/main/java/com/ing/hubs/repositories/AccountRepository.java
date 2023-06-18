package com.ing.hubs.repositories;

import com.ing.hubs.models.Account;
import com.ing.hubs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    List<Account> findAllByUserId(int userId);



    Optional<Account> findByCurrencyAndUserId(String currency, int userId);

    Account findByCurrencyAndUser(String currency, User user);


    Account getAccountById(Integer id);
}
