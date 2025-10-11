package io.github.Rednatsumic.peliculas.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Token de verificación de correo
 * - token: UUID único enviado al usuario
 * - userId: id del usuario asociado
 * - expiresAt: fecha de expiración
 * - used: marca si ya fue utilizado
 */
@Entity
@Table(name = "verification_tokens", indexes = {
    @Index(name = "idx_token", columnList = "token", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(nullable = false)
    private Long userId;

    /** Código de verificación de 6 dígitos para ingreso manual */
    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    public static VerificationToken forUser(Long userId, long ttlSeconds){
        // Generamos un código de 6 dígitos con ceros a la izquierda si hace falta
        int raw = (int)(Math.random() * 1_000_000);
        String six = String.format("%06d", raw);
        return VerificationToken.builder()
            .token(UUID.randomUUID().toString())
            .userId(userId)
            .code(six)
            .expiresAt(Instant.now().plusSeconds(ttlSeconds))
            .used(false)
            .build();
    }
}
