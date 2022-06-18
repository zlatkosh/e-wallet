package com.zlatkosh.ewallet.model.controller;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BalanceIncreaseDto implements Serializable {
    public static final String DEPOSIT_WIN = "DEPOSIT|WIN";

    @NotNull @NotEmpty @Pattern( regexp = DEPOSIT_WIN)
    private String txType;
    @NotNull @Positive @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal transactionAmount;
}
