package com.walletservice.service;

import com.walletservice.AbstractIntegrationTest;
import com.walletservice.dto.WalletOperationRequest;
import com.walletservice.exception.InsufficientFundsException;
import com.walletservice.exception.WalletNotFoundException;
import com.walletservice.entity.Wallet;
import com.walletservice.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class WalletServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    private Wallet createWallet(long balance) {
        return walletRepository.save(new Wallet(UUID.randomUUID(), balance));
    }

    private WalletOperationRequest deposit(UUID id, long amount) {
        return new WalletOperationRequest(id,
                WalletOperationRequest.OperationType.DEPOSIT,
                amount);
    }

    private WalletOperationRequest withdraw(UUID id, long amount) {
        return new WalletOperationRequest(id,
                WalletOperationRequest.OperationType.WITHDRAW,
                amount);
    }

    private void runConcurrent(int threads, Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Test
    void getBalance_WhenWalletExists_ReturnsBalance() {
        Wallet wallet = createWallet(500);

        long balance = walletService.getBalance(wallet.getId());

        assertEquals(500, balance);
    }

    @Test
    void getBalance_WhenWalletNotExists_ThrowsException() {
        assertThrows(WalletNotFoundException.class,
                () -> walletService.getBalance(UUID.randomUUID()));
    }

    @Test
    void processOperation_WhenDeposit_IncreasesBalance() {
        Wallet wallet = createWallet(100);

        walletService.processOperation(deposit(wallet.getId(), 50));

        assertEquals(150, walletRepository.findById(wallet.getId()).orElseThrow().getBalance());
    }

    @Test
    void processOperation_WhenWithdraw_DecreasesBalance() {
        Wallet wallet = createWallet(200);

        walletService.processOperation(withdraw(wallet.getId(), 100));

        assertEquals(100, walletRepository.findById(wallet.getId()).orElseThrow().getBalance());
    }

    @Test
    void processOperation_WhenInsufficientFunds_ThrowsException() {
        Wallet wallet = createWallet(50);

        assertThrows(InsufficientFundsException.class, () -> walletService.processOperation(withdraw(wallet.getId(), 100)));
    }

    @Test
    void processOperation_WhenWalletNotExists_ThrowsException() {
        assertThrows(WalletNotFoundException.class, () -> walletService.processOperation(deposit(UUID.randomUUID(), 100)));
    }

    @Test
    void processOperation_WhenConcurrentDeposits_AllDepositsApplied() throws InterruptedException {
        Wallet wallet = createWallet(0);

        runConcurrent(100, () -> walletService.processOperation(deposit(wallet.getId(), 10)));

        assertEquals(1000, walletRepository.findById(wallet.getId()).orElseThrow().getBalance());
    }

    @Test
    void processOperation_WhenConcurrentWithdraw_BalanceNeverNegative() throws InterruptedException {
        Wallet wallet = createWallet(100);

        runConcurrent(20, () -> {
            try {
                walletService.processOperation(withdraw(wallet.getId(), 10));
            } catch (InsufficientFundsException ignored) {
            }
        });

        long balance = walletRepository.findById(wallet.getId()).orElseThrow().getBalance();
        assertTrue(balance >= 0);
    }
}