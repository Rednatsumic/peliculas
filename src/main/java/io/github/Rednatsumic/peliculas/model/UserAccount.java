package io.github.Rednatsumic.peliculas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad de usuario de la plataforma
 * Campos clave: username, email, passwordHash, plan y enabled.
 */
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Plan plan;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Preferencia del usuario para recibir notificaciones de nuevos estrenos.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean notifyNewReleases = true;
}
