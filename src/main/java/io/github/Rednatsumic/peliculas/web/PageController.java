package io.github.Rednatsumic.peliculas.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.Rednatsumic.peliculas.model.Plan;
import io.github.Rednatsumic.peliculas.service.AccountService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador MVC de páginas públicas y protegidas
 * - /         -> index.html (pública)
 * - /login    -> login.html (pública)
 * - /register -> register.html (GET/POST, pública)
 * - /app/catalog -> catalog.html (requiere autenticación)
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final AccountService accountService;

    @GetMapping({"/", "/home"})
    public String home() {
        // Usamos el index estático por ahora (Live Server),
        // pero la versión Thymeleaf iría en templates si lo preferimos.
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // Por defecto mostramos la pestaña de Ingresar
        model.addAttribute("showRegister", false);
        return "login";
    }

    @GetMapping({"/register", "/signup"})
    public String register(Model model) {
        // Usamos la misma vista que login pero activando la pestaña de Registro
        model.addAttribute("showRegister", true);
        return "login";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam("plan") Plan plan,
                             @RequestParam(value="notify", required=false) String notify,
                             Model model) {
        try {
            // Guardamos preferencia de notificaciones al registrar
            boolean wantsNotify = (notify != null);
            accountService.register(username, email, password, plan, wantsNotify);
            // Nota: por simplicidad, actualizaremos la preferencia luego en AccountService (pendiente)
            model.addAttribute("message", "Te enviamos un email para confirmar tu cuenta. Revisa tu bandeja");
            model.addAttribute("showRegister", false);
            return "login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("showRegister", true);
            return "login";
        }
    }

    /**
     * Enlace de verificación de cuenta
     * - recibe el token por query string
     * - muestra mensaje en pantalla de login
     */
    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, Model model){
        try {
            accountService.verify(token);
            model.addAttribute("message", "Cuenta verificada. Ya podés iniciar sesión.");
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("showRegister", false);
        return "login";
    }

    /**
     * Verificación por código (6 dígitos) — formulario simple
     */
    @GetMapping("/verify-code")
    public String verifyCodeForm(){ return "verify"; }

    @PostMapping("/verify-code")
    public String doVerifyCode(@RequestParam("code") String code, Model model){
        try {
            accountService.verifyCode(code);
            model.addAttribute("message", "Cuenta verificada por código. Iniciá sesión.");
            model.addAttribute("showRegister", false);
            return "login";
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "verify";
        }
    }

    @GetMapping("/app/catalog")
    public String catalog(Model model) {
        model.addAttribute("username", "demo");
        return "catalog";
    }
}
