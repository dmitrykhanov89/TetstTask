package TestTask.controller;

import TestTask.dto.WalletBalanceResponse;
import TestTask.dto.WalletOperationRequest;
import TestTask.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallet")
    public void operate(@RequestBody @Valid WalletOperationRequest request) {
        walletService.processOperation(request);
    }

    @GetMapping("/wallets/{walletId}")
    public WalletBalanceResponse getBalance(@PathVariable UUID walletId) {
        long balance = walletService.getBalance(walletId);
        return new WalletBalanceResponse(walletId, balance);
    }
}
