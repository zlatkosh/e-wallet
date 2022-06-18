package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.controller.TransactionDto;
import com.zlatkosh.ewallet.model.exception.EWalletException;
import com.zlatkosh.ewallet.service.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public void createMonetaryTx(String username, TransactionDto inputTx) {
        try {
            TransactionDto walletTx = walletRepository.doTransaction(username, inputTx);
            if (walletTx.getNewBalance().compareTo(new BigDecimal(0)) < 0) {
                throw new EWalletException("Insufficient funds! You tried to take %.2f from you wallet when your current balance is %.2f."
                        .formatted(walletTx.getTxAmount(), walletTx.getOldBalance()));
            }
            transactionRepository.createTransactionRecord(walletTx);
        } catch (EmptyResultDataAccessException e) {
            String message = "The current user does not have a wallet created. Please create a wallet before attempting transactions!";
            log.error(message, e);
            throw new EWalletException(message);
        }
    }
}
