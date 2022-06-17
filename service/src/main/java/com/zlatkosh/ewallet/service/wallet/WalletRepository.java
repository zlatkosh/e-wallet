package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.model.db.Transaction;

public interface WalletRepository {
    void insertWallet(String username);
    Transaction doTransaction(String username, Transaction transaction);
}