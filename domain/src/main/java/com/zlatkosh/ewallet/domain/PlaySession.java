package com.zlatkosh.ewallet.domain;

public record PlaySession(
        long sessionId,
        String username,
        java.sql.Timestamp startTime,
        java.sql.Timestamp endTime
) {
}
