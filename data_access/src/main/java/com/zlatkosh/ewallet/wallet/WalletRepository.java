package com.zlatkosh.ewallet.wallet;

import com.zlatkosh.ewallet.model.controller.TransactionDto;

public interface WalletRepository {
    void insertWallet(String username);
    TransactionDto doTransaction(String username, TransactionDto transactionDto);
}