package com.zlatkosh.ewallet.service.history;

import com.zlatkosh.ewallet.model.db.PlayerTransactionHistory;

import java.util.stream.Stream;

public interface TransactionHistoryRepository {
    Stream<PlayerTransactionHistory> getPlayerTransactionHistory(String username);
}
