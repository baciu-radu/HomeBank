package com.ing.hubs.services;

import com.ing.hubs.models.ExchangeRate;
import com.ing.hubs.repositories.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public ExchangeRate getExchangeRateRepositoryForCurrencies(String inputCurrency, String outputCurrency) {
        return exchangeRateRepository.findByInputCurrencyAndOutputCurrency(inputCurrency, outputCurrency).get();
    }
}
