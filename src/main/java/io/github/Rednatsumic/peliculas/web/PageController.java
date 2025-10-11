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
    public String login() {
        return "login";
    }

    @GetMapping({"/register", "/signup"})
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam("plan") Plan plan,
                             Model model) {
        try {
            accountService.register(username, email, password, plan);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/app/catalog")
    public String catalog(Model model) {
        model.addAttribute("username", "demo");
        return "catalog";
    }
}
