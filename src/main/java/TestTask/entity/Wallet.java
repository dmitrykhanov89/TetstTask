package TestTask.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    private UUID id;

    @Column(nullable = false)
    private long balance; // хранится в минимальной единице валюты
}
