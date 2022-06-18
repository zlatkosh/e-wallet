package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.controller.TransactionDto;

public interface TransactionRepository {

    void createTransactionRecord(TransactionDto transactionDto);
}
