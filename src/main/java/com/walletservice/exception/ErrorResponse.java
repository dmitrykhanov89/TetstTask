package com.walletservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO для описания ошибки в ответе REST API.
 * <p>
 * Содержит сообщение об ошибке и временную метку её возникновения.
 * Используется для стандартизированных ответов при некорректных запросах,
 * несуществующих кошельках, недостатке средств и других ошибках.
 * </p>
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    /** Сообщение об ошибке */
    private String message;

    /** Временная метка возникновения ошибки */
    private LocalDateTime timestamp;
}