package com.fmi.reviews.web.mvc;

import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.service.MovieReviewService;
import com.fmi.reviews.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
public class ReviewsMvcController {

    private final MovieReviewService movieReviewService;
    private final MovieService movieService;

    @Autowired
    public ReviewsMvcController(MovieReviewService movieReviewService, MovieService movieService) {
        this.movieReviewService = movieReviewService;
        this.movieService = movieService;
    }

    @GetMapping("/reviews")
    String getReviews(Model model,
                      @RequestParam("movieId") Long id) {
        Movie movie = movieService.getMovie(id);
        model.addAttribute("reviews", movieReviewService.getReviewsOfMovie(movie));
        model.addAttribute("movieId", id);
        return "reviews";
    }

    @GetMapping("/review-form/{movieId}")
    String addReview(@ModelAttribute("review") MovieReview movieReview,
                     Model model,
                     @PathVariable Long movieId) {
        String title = "Add review";

        model.addAttribute("movieId", movieId);
        model.addAttribute("title", title);
        model.addAttribute("mode", "create");
        return "review-form";
    }

    @GetMapping("/review-form/edit/{movieId}")
    String editReview(Model model,
                      @PathVariable Long movieId) {
        String title = "Edit review";

        Movie movie = movieService.getMovie(movieId);

        model.addAttribute("review", movieReviewService.getReviewsOfMovie(movie));
        model.addAttribute("movieId", movieId);
        model.addAttribute("mode", "edit");
        return "review-form";
    }

    @PostMapping("/review-form/{id}")
    String postReview(@ModelAttribute("review") MovieReview review,
                      BindingResult bindingResult,
                      @ModelAttribute("movieId") Long movieId,
                      Model model,
                      @ModelAttribute("mode") String mode,
                      RedirectAttributes redirectAttributes,
                      UriComponentsBuilder uriComponentsBuilder) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mode", model.getAttribute("mode"));
            redirectAttributes.addFlashAttribute("review", review);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.review", bindingResult);
            return "redirect:review-form";
        }

        MovieReview movieReview = movieReviewService.addReview(review);
        Movie movie = movieService.getMovie(movieId);
        movie.getReviews().add(movieReview);
        movieService.updateMovie(movieId, movie);

        URI uri = uriComponentsBuilder.path("/reviews")
                .query("movieId={id}").buildAndExpand(movieId).toUri();
        return "redirect" + uri.toString();
    }
}
