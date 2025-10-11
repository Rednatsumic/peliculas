package io.github.Rednatsumic.peliculas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 * - Rutas públicas: /, /index.html, assets estáticos, /h2, /register
 * - Rutas protegidas: /app/**
 * - Login con formulario en /login y redirect a /app/catalog
 * - Logout en /logout y redirect a /
 * - Habilita acceso a h2-console (frameOptions sameOrigin) y excluye /h2 de CSRF
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Reglas de autorización y páginas de login/logout
        // - Rutas estáticas y registro: públicas
        // - /app/** requiere autenticación
        // - Login en /login, logout en /logout
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**", "/h2/**", "/register", "/signup").permitAll()
                .requestMatchers("/app/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/app/catalog", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Seguridad adicional
            // - Permitir h2-console en iframes (sameOrigin)
            // - Excluir /h2/** del CSRF para poder usar la consola
            .headers(h -> h.frameOptions(frame -> frame.sameOrigin()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"));
        return http.build();
    }

    // Nota: Spring detectará automáticamente el UserDetailsService de base de datos (DbUserDetailsService)

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
