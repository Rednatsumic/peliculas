package io.github.Rednatsumic.peliculas.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de usuarios para Spring Security que carga credenciales
 * desde la base de datos.
 */
@Service("dbUserDetailsService")
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en la base; si no existe, lanza excepción estándar de Spring Security
        return repo.findByUsername(username)
            .map(ua ->
                // Construye un UserDetails con rol por defecto "USER"
                User.withUsername(ua.getUsername())
                    .password(ua.getPasswordHash())
                    .roles("USER")
                    // Si la cuenta no está verificada, quedará deshabilitada y no permitirá login
                    .disabled(!ua.isEnabled())
                    .build()
            )
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
