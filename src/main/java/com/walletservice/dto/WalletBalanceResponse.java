package com.walletservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

/**
 * DTO для ответа с балансом кошелька.
 * <p>
 * Содержит идентификатор кошелька и текущий баланс.
 * Используется в ответе на запрос получения баланса.
 * </p>
 */
@Data
@AllArgsConstructor
public class WalletBalanceResponse {
    /** Идентификатор кошелька */
    private UUID walletId;

    /** Текущий баланс кошелька */
    private long balance;
}