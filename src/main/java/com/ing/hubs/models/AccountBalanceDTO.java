package com.ing.hubs.models;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private String currency;
    private Double balance;
}