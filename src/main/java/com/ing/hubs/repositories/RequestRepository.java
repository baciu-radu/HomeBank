package com.ing.hubs.repositories;

import com.ing.hubs.models.Account;
import com.ing.hubs.models.Request;
import com.ing.hubs.models.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllBySenderAccount(Account account);
    List<Request> findAllBySenderAccountAndRequestStatus(Account account, RequestStatus requestStatus);

    Optional<Request> findById(Integer id);

}
