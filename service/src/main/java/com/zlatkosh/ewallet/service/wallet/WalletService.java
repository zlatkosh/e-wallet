package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.model.exception.EWalletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
@Slf4j
class WalletService {
    private final WalletRepository walletRepository;

    void createNewWallet(String username) {
        try {
            walletRepository.insertWallet(username);
        } catch (DuplicateKeyException e) {
            String message = "Failed to create a wallet for user '%s'. The user already has an existing wallet!";
            log.error(message);
            throw new EWalletException( message.formatted(username));
        } catch (Exception e) {
            String message = "Failed to create a wallet for user '%s'.".formatted(username);
            log.error(message, e);
            throw new EWalletException(message, e);
        }
    }
}