package com.zlatkosh.ewallet.wallet;

import com.zlatkosh.ewallet.wallet.WalletRepository;
import com.zlatkosh.ewallet.model.exception.EWalletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;

    void createNewWallet(String username) {
        try {
            walletRepository.insertWallet(username);
        } catch (DuplicateKeyException e) {
            String message = "Failed to create a wallet for user '%s'. The user already has an existing wallet!";
            log.error(message);
            throw new EWalletException( message.formatted(username));
        }
    }
}