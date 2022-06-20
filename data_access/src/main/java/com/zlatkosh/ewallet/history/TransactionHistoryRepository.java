package com.zlatkosh.ewallet.history;

import com.zlatkosh.ewallet.model.db.PlayerTransactionHistory;

import java.util.stream.Stream;

interface TransactionHistoryRepository {
    Stream<PlayerTransactionHistory> getPlayerTransactionHistory(String username);
}
