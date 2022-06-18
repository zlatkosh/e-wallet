package com.zlatkosh.ewallet.model.controller;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BalanceDecreaseDto implements Serializable {
    public static final String WITHDRAW_BET = "WITHDRAW|BET";

    @NotNull @NotEmpty @Pattern( regexp = WITHDRAW_BET)
    private String txType;
    @NotNull @Positive @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal transactionAmount;
}
