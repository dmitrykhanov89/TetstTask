package com.walletservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Сущность кошелька.
 * <p>
 * Хранит идентификатор кошелька и текущий баланс.
 * Используется для операций пополнения и списания средств.
 * </p>
 */
@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    /** Идентификатор кошелька */
    @Id
    private UUID id;

    /** Текущий баланс кошелька */
    @Column(nullable = false)
    private long balance;
}