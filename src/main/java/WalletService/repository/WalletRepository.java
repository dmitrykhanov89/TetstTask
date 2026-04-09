package WalletService.repository;

import WalletService.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью Wallet.
 * <p>
 * Предоставляет стандартные методы JPA для CRUD операций,
 * а также метод с блокировкой записи для безопасной работы в конкурентной среде.
 * </p>
 */
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Получает кошелек по идентификатору с блокировкой записи.
     * <p>
     * Используется для безопасного обновления баланса при высоких нагрузках.
     * </p>
     *
     * @param id идентификатор кошелька
     * @return Optional с кошельком, если найден
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Optional<Wallet> findByIdForUpdate(UUID id);
}