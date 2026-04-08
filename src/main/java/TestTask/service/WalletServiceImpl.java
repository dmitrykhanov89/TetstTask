package TestTask.service;

import TestTask.dto.WalletOperationRequest;
import TestTask.exception.InsufficientFundsException;
import TestTask.exception.WalletNotFoundException;
import TestTask.model.Wallet;
import TestTask.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public void processOperation(WalletOperationRequest request) {
        UUID walletId = request.getWalletId();
        Wallet wallet = walletRepository.findById(walletId)
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
