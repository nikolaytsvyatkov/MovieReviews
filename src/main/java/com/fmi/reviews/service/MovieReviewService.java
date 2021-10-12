package com.fmi.reviews.service;

import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.model.User;

import java.util.List;

public interface MovieReviewService {
    List<MovieReview> getReviews();
    MovieReview deleteReview(Long id);
    MovieReview getReview(Long id);
    MovieReview addReview(MovieReview review);
    MovieReview updateReview(Long id, MovieReview review);
    long getReviewsCount();
    List<MovieReview> getReviewsOfMovie(Movie movie);
}
