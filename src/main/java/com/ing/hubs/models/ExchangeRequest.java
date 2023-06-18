package com.ing.hubs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequest {
    private Double amount;
    private Integer initializerAccountId;
    private Integer targetAccountId;
}
