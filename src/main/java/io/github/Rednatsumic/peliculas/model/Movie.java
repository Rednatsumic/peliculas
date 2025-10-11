package io.github.Rednatsumic.peliculas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Película simple para el catálogo.
 */
@Entity
@Table(name = "movies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private Integer year;
    private String genre;
    private String posterUrl;
    private String trailerUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Plan planMinimo; // BRONCE/PLATA/ORO requerido para verla
}
