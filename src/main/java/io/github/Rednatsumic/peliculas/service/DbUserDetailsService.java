package io.github.Rednatsumic.peliculas.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de usuarios para Spring Security que carga credenciales
 * desde la base de datos (tabla users).
 *
 * Responsabilidades:
 * - Buscar UserAccount por username
 * - Mapear a UserDetails con rol USER
 * - Respetar el flag enabled
 */
@Service("dbUserDetailsService")
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount ua = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new User(
                ua.getUsername(),
                ua.getPasswordHash(),
                ua.isEnabled(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
package io.github.Rednatsumic.peliculas.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;

@Service("dbUserDetailsService")
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username)
            .map(ua -> User.withUsername(ua.getUsername())
                .password(ua.getPasswordHash())
                .roles("USER")
                .disabled(!ua.isEnabled())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
