package com.walletservice.stress;

import com.walletservice.AbstractIntegrationTest;
import com.walletservice.dto.WalletOperationRequest;
import com.walletservice.entity.Wallet;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.WalletService;
import com.walletservice.service.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Стресс-тест для проверки корректной работы {@link WalletServiceImpl}
 * в условиях высокой конкуренции.
 * <p>
 * Тест создаёт один кошелёк и выполняет множество параллельных операций пополнения
 * с использованием {@link ExecutorService} и {@link CountDownLatch}.
 * После завершения всех потоков проверяется, что суммарный баланс кошелька
 * соответствует ожидаемой сумме.
 * </p>
 * <p>
 * Этот тест демонстрирует:
 * <ul>
 *     <li>Потокобезопасность метода {@link WalletServiceImpl#processOperation}</li>
 *     <li>Работу блокировок при одновременном доступе к одному кошельку</li>
 *     <li>Отсутствие потери данных при конкурентных операциях</li>
 * </ul>
 * </p>
 */
class WalletConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void testConcurrentDeposits() throws InterruptedException {
        Wallet wallet = walletRepository.save(new Wallet(UUID.randomUUID(), 0L));
        int threads = 100;
        long depositAmount = 10L;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    walletService.processOperation(new WalletOperationRequest(
                            wallet.getId(),
                            WalletOperationRequest.OperationType.DEPOSIT,
                            depositAmount
                    ));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Wallet updated = walletRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(depositAmount * threads, updated.getBalance());
    }
}