package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.db.Transaction;

public interface TransactionRepository {

    void createTransactionRecord(Transaction transaction);
}
