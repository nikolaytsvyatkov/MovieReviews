package com.fmi.reviews.service;

import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.User;

import java.util.List;

public interface MovieService {
    List<Movie> getMovies();
    Movie deleteMovie(Long id);
    Movie getMovie(Long id);
    Movie addMovie(Movie movie);
    Movie updateMovie(Long id, Movie movie);
    long getMoviesCount();
}
