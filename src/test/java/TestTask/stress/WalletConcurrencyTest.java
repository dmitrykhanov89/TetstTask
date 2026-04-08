package TestTask.stress;

import TestTask.dto.WalletOperationRequest;
import TestTask.model.Wallet;
import TestTask.repository.WalletRepository;
import TestTask.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void testConcurrentDeposits() throws InterruptedException {
        // Создаём тестовый кошелёк
        Wallet wallet = walletRepository.save(new Wallet(UUID.randomUUID(), 0L));

        int threads = 100; // количество параллельных потоков
        long depositAmount = 10L;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch latch = new CountDownLatch(threads);

        // Запускаем все потоки почти одновременно
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

        latch.await(); // ждём пока все потоки завершатся

        Wallet updated = walletRepository.findById(wallet.getId()).orElseThrow();
        assertEquals(depositAmount * threads, updated.getBalance());
    }
}