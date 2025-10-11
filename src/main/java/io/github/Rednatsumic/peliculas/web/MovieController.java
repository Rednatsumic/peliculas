package io.github.Rednatsumic.peliculas.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.Rednatsumic.peliculas.model.Movie;
import io.github.Rednatsumic.peliculas.model.Plan;
import io.github.Rednatsumic.peliculas.service.MovieService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador simple para crear películas (pruebas de notificaciones).
 * Nota: en un entorno real esto debería estar protegido (admin),
 * aquí lo dejamos público para probar rápido el mailing.
 */
@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/admin/new-movie")
    public String form(){ return "new-movie"; }

    @PostMapping("/admin/new-movie")
    public String create(@RequestParam String title,
                         @RequestParam Integer year,
                         @RequestParam String genre,
                         @RequestParam(required=false) String posterUrl,
                         @RequestParam(required=false) String trailerUrl,
                         @RequestParam("planMinimo") Plan plan,
                         Model model){
        Movie m = Movie.builder()
            .title(title).year(year).genre(genre)
            .posterUrl(posterUrl).trailerUrl(trailerUrl)
            .planMinimo(plan)
            .build();
        movieService.createAndNotify(m);
        model.addAttribute("message", "Película creada y notificaciones enviadas (si corresponde)");
        return "new-movie";
    }
}
