package io.github.Rednatsumic.peliculas.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.Rednatsumic.peliculas.model.VerificationToken;

/**
 * Repositorio de tokens de verificación de email.
 * - Permite buscar por el valor de token recibido por enlace.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByCode(String code);
}
