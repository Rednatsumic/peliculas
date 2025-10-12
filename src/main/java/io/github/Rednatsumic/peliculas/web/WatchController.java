package io.github.Rednatsumic.peliculas.web;

import io.github.Rednatsumic.peliculas.model.Movie;
import io.github.Rednatsumic.peliculas.model.Plan;
import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.repo.MovieRepository;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/app/watch")
public class WatchController {
    private final MovieRepository movies;
    private final UserAccountRepository users;

    @GetMapping("/{id}")
    public String watch(@PathVariable Long id, Authentication auth, Model model){
        Movie m = movies.findById(id).orElse(null);
        if (m == null){
            model.addAttribute("error", "No encontramos la película");
            return "catalog";
        }
        UserAccount ua = users.findByUsername(auth.getName()).orElseThrow();
        if (!ua.isSubscriptionActive()){
            // Upsell: pedir método de pago antes de permitir ver
            model.addAttribute("upsell", "Para ver películas, primero cargá un método de pago.");
            model.addAttribute("movie", m);
            return "upsell";
        }
        if (!hasPlan(ua.getPlan(), m.getPlanMinimo())){
            model.addAttribute("upsell", "Tu plan actual no incluye este contenido. Mejora tu plan para continuar.");
            model.addAttribute("movie", m);
            return "upsell";
        }
        model.addAttribute("movie", m);
        return "watch";
    }

    private boolean hasPlan(Plan user, Plan required){
        int u = rank(user); int r = rank(required);
        return u >= r;
    }
    private int rank(Plan p){
        return switch (p){
            case BRONCE -> 1;
            case PLATA -> 2;
            case ORO -> 3;
        };
    }
}
