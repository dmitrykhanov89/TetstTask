package WalletService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * DTO для запроса операции с кошельком.
 * <p>
 * Содержит идентификатор кошелька, тип операции (DEPOSIT или WITHDRAW) и сумму.
 * Поля валидируются с помощью аннотаций {@link NotNull} и {@link Positive}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperationRequest {

    /** Идентификатор кошелька */
    @NotNull
    private UUID walletId;

    /** Тип операции: пополнение или списание */
    @NotNull(message = "Operation type must be DEPOSIT or WITHDRAW")
    private OperationType operationType;

    /** Сумма операции, должна быть положительной */
    @Positive(message = "Amount must be positive")
    private long amount;

    /** Тип операции с кошельком */
    public enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
}