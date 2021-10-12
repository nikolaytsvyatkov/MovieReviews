package com.fmi.reviews.web.rest;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.service.MovieReviewService;
import com.fmi.reviews.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static com.fmi.reviews.utils.ErrorHandlingUtils.getViolationsAsStringList;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @DeleteMapping("/{id}")
    public Movie deleteMovie(@PathVariable Long id){
        return movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id){
        return movieService.getMovie(id);
    }

    @GetMapping
    public List<Movie> getMovies(){
        return movieService.getMovies();
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid movie data", getViolationsAsStringList(errors));
        }

        Movie addedMovie = movieService.addMovie(movie);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(addedMovie.getId()).toUri()
        ).body(addedMovie);
    }

    @PutMapping("/{id}")
    public  Movie updateMovie(@Valid @RequestBody Movie movie, @PathVariable Long id, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid movie data", getViolationsAsStringList(errors));
        }

        return movieService.updateMovie(id, movie);
    }
}
