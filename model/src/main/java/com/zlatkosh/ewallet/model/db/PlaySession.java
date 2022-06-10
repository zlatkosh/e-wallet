package com.zlatkosh.ewallet.model.db;

public record PlaySession(
        long sessionId,
        String username,
        java.sql.Timestamp startTime,
        java.sql.Timestamp endTime
) {
}
