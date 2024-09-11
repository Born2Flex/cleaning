package ua.edu.ukma.cleaning.utils.security.refresh.tokens;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.cleaning.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiryDate;

    @OneToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
