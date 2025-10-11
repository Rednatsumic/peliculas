package io.github.Rednatsumic.peliculas.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.Rednatsumic.peliculas.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
