package TestTask.service;

import TestTask.dto.WalletOperationRequest;
import java.util.UUID;

public interface WalletService {

    void processOperation(WalletOperationRequest request);

    long getBalance(UUID walletId);
}
