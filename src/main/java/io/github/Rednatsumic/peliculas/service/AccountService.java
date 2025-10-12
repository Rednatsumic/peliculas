package io.github.Rednatsumic.peliculas.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.Rednatsumic.peliculas.model.Plan;
import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.model.VerificationToken;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import io.github.Rednatsumic.peliculas.repo.VerificationTokenRepository;
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
    private final VerificationTokenRepository tokenRepo;
    private final MailService mailService;

    public static final long TOKEN_TTL_SECONDS = 24 * 3600; // 24h
    private static final java.util.regex.Pattern USERNAME_PATTERN = java.util.regex.Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final java.util.regex.Pattern PASSWORD_PATTERN = java.util.regex.Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%&*._-]).{8,64}$");
    private static final java.util.Set<String> BANNED = java.util.Set.of(
        // Lista corta de palabras a evitar (ampliable). Se valida sobre username normalizado (sin tildes).
        "mierda", "idiota", "tonto", "estupido", "puta", "puto", "caca", "pedo", "xxx", "porn"
    );

    /**
     * Registro de usuario con verificación por email.
     * - Crea el usuario deshabilitado
     * - Genera un token y lo envía por correo
     */
    @Transactional
    public UserAccount register(String username, String email, String rawPassword, Plan plan, boolean notify) {
        // Validaciones de username/email/password en servidor
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Usuario inválido: usa 3-20 caracteres (letras, números, punto, guion o guion bajo)");
        }
        if (containsBanned(username) || isReserved(username)) {
            throw new IllegalArgumentException("El nombre de usuario no está permitido. Elegí otro por favor");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (rawPassword == null || !PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException("Contraseña inválida: mínimo 8 caracteres e incluir minúscula, MAYÚSCULA, número y al menos un símbolo (@, !, #, $, %, &, *, ., _ o -)");
        }
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
            .enabled(false)
            .notifyNewReleases(notify)
            .build();
        ua = repo.save(ua);

        // Generar token y enviar email
    // Generamos un token con UUID y un código de 6 dígitos
    VerificationToken token = VerificationToken.forUser(ua.getId(), TOKEN_TTL_SECONDS);
        tokenRepo.save(token);
        String verifyUrl = "http://localhost:8080/verify?token=" + token.getToken();
    String body = "Hola " + username + ",\n\n" +
        "¡Gracias por registrarte! Tenés dos opciones para verificar tu cuenta:\n" +
        "1) Clic en: " + verifyUrl + "\n" +
        "2) Ingresá este código en la pantalla de verificación: " + token.getCode() + "\n\n" +
        "Si no fuiste vos, ignorá este mensaje.";
        mailService.send(email, "Confirma tu correo — Películas", body);
        return ua;
    }

    private boolean containsBanned(String s){
        if (s == null) return false;
        String norm = normalize(s);
        for (String b : BANNED){
            if (norm.contains(b)) return true;
        }
        return false;
    }
    private boolean isReserved(String s){
        String v = s.toLowerCase();
        return v.startsWith("admin") || v.startsWith("root") || v.equals("soporte") || v.equals("support");
    }
    private String normalize(String input){
        String n = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase();
    }

    /**
     * Verifica el token recibido por email, habilitando la cuenta.
     */
    @Transactional
    public boolean verify(String token) {
        VerificationToken vt = tokenRepo.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (vt.isUsed() || vt.getExpiresAt().isBefore(java.time.Instant.now())) {
            throw new IllegalArgumentException("Token expirado o ya usado");
        }
        UserAccount ua = repo.findById(vt.getUserId())
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para el token"));
        ua.setEnabled(true);
        repo.save(ua);
        vt.setUsed(true);
        tokenRepo.save(vt);
        return true;
    }

    /**
     * Verificación alternativa por código de 6 dígitos (sin link).
     */
    @Transactional
    public boolean verifyCode(String code) {
        VerificationToken vt = tokenRepo.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
        if (vt.isUsed() || vt.getExpiresAt().isBefore(java.time.Instant.now())) {
            throw new IllegalArgumentException("Código expirado o ya usado");
        }
        UserAccount ua = repo.findById(vt.getUserId())
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para el código"));
        ua.setEnabled(true);
        repo.save(ua);
        vt.setUsed(true);
        tokenRepo.save(vt);
        return true;
    }
}
