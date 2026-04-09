package com.walletservice.exception;

/**
 * Исключение, выбрасываемое при попытке обращения к несуществующему кошельку.
 * <p>
 * Обрабатывается {@link GlobalExceptionHandler} и возвращает HTTP 404 Not Found.
 * </p>
 */
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}