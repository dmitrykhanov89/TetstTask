package TestTask.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull(message = "Operation type must be DEPOSIT or WITHDRAW")
    private OperationType operationType;

    @Positive(message = "Amount must be positive")
    private long amount;

    public enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
}