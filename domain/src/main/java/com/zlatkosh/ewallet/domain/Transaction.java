package com.zlatkosh.ewallet.domain;

public record Transaction(
        long txId,
        String txType,
        long sessionId,
        java.sql.Timestamp txTime,
        String oldBalance,
        String newBalance
) {
}
