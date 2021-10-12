package com.fmi.reviews.dao;

import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {
    List<MovieReview> findByMovie(Movie movie);
}
