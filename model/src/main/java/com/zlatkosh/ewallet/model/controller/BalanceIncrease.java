package com.zlatkosh.ewallet.model.controller;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BalanceIncrease implements Serializable {

    @NotNull @NotEmpty @Pattern( regexp = "DEPOSIT|WIN")
    private String txType;
    @NotNull @Positive
    private BigDecimal transactionAmount;
}
