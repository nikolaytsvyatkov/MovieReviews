package com.fmi.reviews.service.impl;

import com.fmi.reviews.dao.MovieReviewRepository;
import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.exception.UnautorizedRequestException;
import com.fmi.reviews.exception.UnexistingEntityException;
import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.model.Role;
import com.fmi.reviews.model.User;
import com.fmi.reviews.service.MovieReviewService;
import com.fmi.reviews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovieReviewServiceImpl implements MovieReviewService {

    private MovieReviewRepository movieReviewRepository;
    private UserService userService;

    @Autowired
    public MovieReviewServiceImpl(MovieReviewRepository movieReviewRepository, UserService userService) {
        this.movieReviewRepository = movieReviewRepository;
        this.userService = userService;
    }

    @Override
    public List<MovieReview> getReviews() {
        return movieReviewRepository.findAll();
    }

    @Override
    public MovieReview deleteReview(Long id) {
        MovieReview review = getReview(id);
        User user = getLoggedInUser();

        if (user.getRole() == Role.REVIEWER && !review.getUser().getId().equals(user.getId())) {
            throw new UnautorizedRequestException("Unauthorized delete");
        }

        movieReviewRepository.deleteById(id);
        return review;
    }

    @Override
    public MovieReview getReview(Long id) {
        return movieReviewRepository.findById(id).orElseThrow(() -> new UnexistingEntityException(String.format("MovieReview with id %s, does not exist",
                id)));
    }

    @Override
    public MovieReview addReview(MovieReview review) {
        review.setId(null);

        User user = getLoggedInUser();
        review.setUser(user);

        MovieReview res =  movieReviewRepository.saveAndFlush(review);
        user.getReviews().add(review);
        userService.updateUser(user.getId(), user);

        return res;
    }

    @Override
    public MovieReview updateReview(Long id, MovieReview review) {
        if (!review.getId().equals(id)) {
            throw new InvalidEntityDataException("Review url id defers from body recipe id");
        }


        User user = getLoggedInUser();
        boolean find = false;
        for(MovieReview mReview : user.getReviews()){
            if (mReview.getId().equals(id)) {
                find = true;
                break;
            }
        }

        if (user.getRole() == Role.REVIEWER && !find) {
            throw new UnautorizedRequestException("Unauthorized update");
        }

        getReview(id);
        review.setModified(LocalDateTime.now());
        review.setUser(user);
        return movieReviewRepository.save(review);
    }

    @Override
    public long getReviewsCount() {
        return movieReviewRepository.count();
    }

    @Override
    public List<MovieReview> getReviewsOfMovie(Movie movie) {
        return movieReviewRepository.findByMovie(movie);
    }

    private User getLoggedInUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(name);

        return user;
    }
}
