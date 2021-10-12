package com.fmi.reviews.web.rest;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.model.User;
import com.fmi.reviews.service.MovieReviewService;
import com.fmi.reviews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

import static com.fmi.reviews.utils.ErrorHandlingUtils.getViolationsAsStringList;

@RestController
@RequestMapping("/api/reviews")
public class MovieReviewController {
    private MovieReviewService reviewService;

    @Autowired
    public MovieReviewController(MovieReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @DeleteMapping("/{id}")
    public MovieReview deleteReview(@PathVariable Long id){
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public MovieReview getReviewById(@PathVariable Long id){
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<MovieReview> getReviews(){
        return reviewService.getReviews();
    }

    @PostMapping
    public ResponseEntity<MovieReview> addReview(@Valid @RequestBody MovieReview review, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid review data", getViolationsAsStringList(errors));
        }

        MovieReview movieReview = reviewService.addReview(review);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(movieReview.getId()).toUri()
        ).body(movieReview);
    }

    @PutMapping("/{id}")
    public  MovieReview updateReview(@Valid @RequestBody MovieReview review, @PathVariable Long id, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid movie data", getViolationsAsStringList(errors));
        }

        return reviewService.updateReview(id, review);
    }
}
