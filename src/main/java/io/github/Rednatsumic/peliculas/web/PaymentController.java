package io.github.Rednatsumic.peliculas.web;

import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;

/**
 * Flujo simple de método de pago (demo):
 * - GET  /app/payment -> muestra formulario para cargar tarjeta
 * - POST /app/payment -> guarda los últimos 4 dígitos y marca suscripción activa
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/app/payment")
public class PaymentController {

    private final UserAccountRepository users;

    @GetMapping
    public String paymentForm(Authentication auth, Model model) {
        String username = auth.getName();
        UserAccount ua = users.findByUsername(username).orElseThrow();
        if (ua.isSubscriptionActive()) {
            model.addAttribute("message", "Ya tenés un método de pago cargado.");
        }
        model.addAttribute("payment", new PaymentRequest());
        return "payment";
    }

    @PostMapping
    public String savePayment(Authentication auth, @ModelAttribute("payment") PaymentRequest req, Model model) {
        String username = auth.getName();
        UserAccount ua = users.findByUsername(username).orElseThrow();
        // Validaciones mínimas (demo):
        if (req.cardNumber == null || req.cardNumber.replaceAll("\\s+", "").length() < 12) {
            model.addAttribute("error", "Número de tarjeta inválido");
            return "payment";
        }
        String digits = req.cardNumber.replaceAll("\\D", "");
        ua.setCardBrand(detectBrand(digits));
        ua.setCardLast4(digits.substring(Math.max(0, digits.length()-4)));
        ua.setSubscriptionActive(true);
        ua.setPaymentAddedAt(Instant.now());
        users.save(ua);
        model.addAttribute("message", "Método de pago cargado. ¡Ya podés ver películas!");
        return "payment";
    }

    private String detectBrand(String digits){
        if (digits.startsWith("4")) return "VISA";
        if (digits.matches("5[1-5].*")) return "MASTERCARD";
        if (digits.matches("3[47].*")) return "AMEX";
        return "CARD";
    }

    @Data
    public static class PaymentRequest {
        public String cardHolder;
        public String cardNumber;
        public String expMonth;
        public String expYear;
        public String cvc;
    }
}
