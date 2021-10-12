package com.fmi.reviews.service.impl;

import com.fmi.reviews.dao.MovieRepository;
import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.exception.UnexistingEntityException;
import com.fmi.reviews.model.Movie;
import com.fmi.reviews.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie deleteMovie(Long id) {
        Movie review = getMovie(id);
        movieRepository.deleteById(id);

        return review;
    }

    @Override
    public Movie getMovie(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new UnexistingEntityException(String.format("Movie with id %s, does not exist",
                id)));
    }

    @Override
    public Movie addMovie(Movie movie) {
        movie.setId(null);

        return movieRepository.saveAndFlush(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        if (!movie.getId().equals(id)) {
            throw new InvalidEntityDataException("Movie url id defers from body movie id");
        }

        getMovie(id);

        movie.setModified(LocalDateTime.now());
        return movieRepository.save(movie);
    }

    @Override
    public long getMoviesCount() {
        return movieRepository.count();
    }
}
