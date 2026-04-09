package WalletService.service;

import WalletService.dto.WalletOperationRequest;
import java.util.UUID;

/**
 * Сервис для работы с кошельками.
 * <p>
 * Позволяет выполнять операции пополнения и списания средств,
 * а также получать текущий баланс кошелька.
 * </p>
 */
public interface WalletService {

    /**
     * Обрабатывает операцию с кошельком (DEPOSIT или WITHDRAW).
     *
     * @param request данные операции: идентификатор кошелька, тип и сумма
     */
    void processOperation(WalletOperationRequest request);

    /**
     * Возвращает текущий баланс кошелька.
     *
     * @param walletId идентификатор кошелька
     * @return баланс кошелька
     */
    long getBalance(UUID walletId);
}