package com.zlatkosh.ewallet.model.exception;

public class EWalletException extends RuntimeException {
    public EWalletException(String message) {
        super(message);
    }

    public EWalletException(String message, Throwable cause) {
        super(message, cause);
    }
}
