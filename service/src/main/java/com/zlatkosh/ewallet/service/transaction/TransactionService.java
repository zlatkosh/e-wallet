package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.db.Transaction;
import com.zlatkosh.ewallet.model.exception.EWalletException;
import com.zlatkosh.ewallet.service.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public void createIncreaseBalanceTx(String username, Transaction inputTx) {
        try {
            Transaction walletTx = walletRepository.doTransaction(username, inputTx);
            transactionRepository.createTransactionRecord(walletTx);
        } catch (EmptyResultDataAccessException e) {
            String message = "The current user does not have a wallet created. Please create a wallet before attempting transactions!";
            log.error(message, e);
            throw new EWalletException(message);
        } catch (Exception e) {
            String message = "Failed to execute transaction, for transaction record '%s'.".formatted(inputTx);
            log.error(message, e);
            throw new EWalletException(message);
        }
    }
}
