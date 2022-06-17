package com.zlatkosh.ewallet.model.db;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public final class Transaction {
    private final String txType;
    private final Long sessionId;
    private Date txTime;
    private final BigDecimal txAmount;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
}
