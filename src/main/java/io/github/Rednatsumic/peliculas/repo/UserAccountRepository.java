package io.github.Rednatsumic.peliculas.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.Rednatsumic.peliculas.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
