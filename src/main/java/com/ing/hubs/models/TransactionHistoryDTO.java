package com.ing.hubs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDTO {
    private Double amount;
    private String currency;
    private String type;
    private LocalDate date;
    private Account initializerAccount;
    private Account targetAccount;

}
