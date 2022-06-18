package com.zlatkosh.ewallet.model.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public final class TransactionDto {
    private final Long txId;
    private final String txType;
    @JsonIgnore
    private final Long sessionId;
    private Date txTime;
    private final BigDecimal txAmount;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
}
