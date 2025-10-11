package io.github.Rednatsumic.peliculas.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.Rednatsumic.peliculas.model.Plan;
import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de cuentas
 * - Valida unicidad de usuario y email
 * - Hashea la contraseña con BCrypt
 * - Persiste el usuario con su plan
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserAccountRepository repo;
    private final PasswordEncoder encoder;

    @Transactional
    public UserAccount register(String username, String email, String rawPassword, Plan plan) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Usuario ya existe");
        }
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        UserAccount ua = UserAccount.builder()
            .username(username)
            .email(email)
            .passwordHash(encoder.encode(rawPassword))
            .plan(plan)
            .enabled(true)
            .build();
        return repo.save(ua);
    }
}
