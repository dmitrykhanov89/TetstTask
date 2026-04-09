package WalletService.controller;

import WalletService.dto.WalletBalanceResponse;
import WalletService.dto.WalletOperationRequest;
import WalletService.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 * REST-контроллер для работы с кошельками.
 * <p>
 * Предоставляет эндпоинты для выполнения операций с кошельком
 * и получения текущего баланса.
 * </p>
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Выполняет операцию с кошельком (DEPOSIT или WITHDRAW).
     *
     * @param request данные операции с кошельком
     */
    @PostMapping("/wallet")
    public void operate(@RequestBody @Valid WalletOperationRequest request) {
        walletService.processOperation(request);
    }

    /**
     * Возвращает текущий баланс кошелька.
     *
     * @param walletId идентификатор кошелька
     * @return DTO с идентификатором кошелька и балансом
     */
    @GetMapping("/wallets/{walletId}")
    public WalletBalanceResponse getBalance(@PathVariable UUID walletId) {
        long balance = walletService.getBalance(walletId);
        return new WalletBalanceResponse(walletId, balance);
    }
}