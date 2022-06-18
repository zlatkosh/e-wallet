package com.zlatkosh.ewallet.model.db;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class PlayerTransactionHistory implements Serializable {
    private final String username;
    private final Long sessionId;
    private final Date sessionStartTime;
    private final Date sessionExpTime;
    private final Long txId;
    private final String txType;
    private final Date txTime;
    private final BigDecimal txAmount;
    private final BigDecimal oldBalance;
    private final BigDecimal newBalance;
}
