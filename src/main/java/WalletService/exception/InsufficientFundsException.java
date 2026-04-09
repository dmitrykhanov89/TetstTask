package WalletService.exception;

/**
 * Исключение, выбрасываемое при попытке списания средств с кошелька,
 * если на балансе недостаточно средств.
 * <p>
 * Обрабатывается {@link GlobalExceptionHandler} и возвращает HTTP 409 Conflict.
 * </p>
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}