package com.zlatkosh.ewallet.transaction;

import com.zlatkosh.ewallet.model.controller.TransactionDto;

interface TransactionRepository {

    void createTransactionRecord(TransactionDto transactionDto);
}
