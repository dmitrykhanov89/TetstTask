package com.walletservice.service;

import com.walletservice.dto.WalletOperationRequest;
import com.walletservice.exception.InsufficientFundsException;
import com.walletservice.exception.WalletNotFoundException;
import com.walletservice.entity.Wallet;
import com.walletservice.repository.WalletRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Реализация сервиса {@link WalletService} для управления кошельками.
 * <p>
 * Предоставляет методы для выполнения операций пополнения и списания средств,
 * а также получения текущего баланса кошелька.
 * </p>
 * <p>
 * Особенности реализации:
 * <ul>
 *     <li>Использует {@link WalletRepository} для доступа к базе данных</li>
 *     <li>Методы обновления баланса помечены {@link Transactional} для атомарности</li>
 *     <li>Метод {@link WalletRepository#findByIdForUpdate(UUID)} обеспечивает блокировку записи для безопасности при высокой конкуренции</li>
 *     <li>При недостатке средств выбрасывает {@link InsufficientFundsException}</li>
 *     <li>Если кошелек не найден, выбрасывает {@link WalletNotFoundException}</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public void processOperation(WalletOperationRequest request) {
        UUID walletId = request.getWalletId();
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        switch (request.getOperationType()) {
            case DEPOSIT -> deposit(wallet, request.getAmount());
            case WITHDRAW -> withdraw(wallet, request.getAmount());
        }
    }

    private void deposit(Wallet wallet, long amount) {
        wallet.setBalance(wallet.getBalance() + amount);
    }

    private void withdraw(Wallet wallet, long amount) {
        if (wallet.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance() - amount);
    }

    @Override
    public long getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"))
                .getBalance();
    }
}
