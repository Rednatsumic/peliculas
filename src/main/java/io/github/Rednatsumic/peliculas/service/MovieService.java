package io.github.Rednatsumic.peliculas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.Rednatsumic.peliculas.model.Movie;
import io.github.Rednatsumic.peliculas.model.UserAccount;
import io.github.Rednatsumic.peliculas.repo.MovieRepository;
import io.github.Rednatsumic.peliculas.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio del catálogo de películas.
 * - Crear nuevas películas
 * - Notificar por email a usuarios que aceptaron recibir novedades
 */
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepo;
    private final UserAccountRepository userRepo;
    private final MailService mailService;

    public List<Movie> listAll(){ return movieRepo.findAll(); }

    @Transactional
    public Movie createAndNotify(Movie m){
        Movie saved = movieRepo.save(m);
        // Enviar notificación a usuarios con notifyNewReleases=true
        List<UserAccount> users = userRepo.findAll();
        for (UserAccount u : users){
            if (u.isEnabled() && u.isNotifyNewReleases()){
                String subject = "Nuevo estreno: " + saved.getTitle();
                String body = "Hola " + u.getUsername() + ",\n\n" +
                        "Se acaba de agregar al catálogo: " + saved.getTitle() + " (" + saved.getYear() + ")\n" +
                        "Género: " + (saved.getGenre()==null?"N/D":saved.getGenre()) + "\n" +
                        "Mirala en el catálogo: http://localhost:8080/app/catalog\n\n" +
                        "— Películas";
                mailService.send(u.getEmail(), subject, body);
            }
        }
        return saved;
    }
}
