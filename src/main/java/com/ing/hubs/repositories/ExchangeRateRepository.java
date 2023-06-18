package com.ing.hubs.repositories;

import com.ing.hubs.models.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    Optional<ExchangeRate> findByInputCurrencyAndOutputCurrency(String inputCurrency, String outputCurrency);
}
