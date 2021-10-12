package com.fmi.reviews.init;

import com.fmi.reviews.dao.MovieRepository;
import com.fmi.reviews.dao.MovieReviewRepository;
import com.fmi.reviews.dao.UserRepository;
import com.fmi.reviews.model.Movie;
import com.fmi.reviews.model.MovieReview;
import com.fmi.reviews.model.Role;
import com.fmi.reviews.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private UserRepository userRepository;

    private MovieRepository movieRepository;

    private MovieReviewRepository movieReviewRepository;

    private PasswordEncoder passwordEncoder;

    private static List<MovieReview> reviews = List.of(new MovieReview("test", 6, "test")
            , new MovieReview("test1", 10, "test1")
            , new MovieReview("test2", 1, "test2"));

    private static List<User> users = List.of(
            new User("admin",
                    "Admin",
                    "Admin",
                    "admin1234*",
                    "admin@gmail.com"
                    , Role.ADMINISTRATOR,
                    Set.of(reviews.get(0))),
            new User("moderator", "Moderator", "Moderator", "moderator1234*", "moderator@gmail.com"
                    , Role.MODERATOR, Set.of(reviews.get(0), reviews.get(1))),
            new User("reviewer", "Reviewer", "Reviewer", "reviewer1234*", "reviewer@gmail.com"
                    , Role.REVIEWER, Set.of(reviews.get(2))));

    private static List<Movie> movies = List.of(
            new Movie("Interstellar ", "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                    Set.of(reviews.get(1), reviews.get(0))),
            new Movie("Forrest Gump", "The presidencies of Kennedy and Johnson, the events of Vietnam, Watergate and other historical events unfold through the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.",
                    Set.of(reviews.get(2))));

    @Autowired
    public DataInitializer(UserRepository userRepository, MovieRepository movieRepository, MovieReviewRepository movieReviewRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.movieReviewRepository = movieReviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (movieRepository.count() == 0) {
            movies.forEach(movieRepository::saveAndFlush);
        }

        if (userRepository.count() == 0) {
            users.forEach(u -> {
                u.setPassword(passwordEncoder.encode(u.getPassword()));

                userRepository.saveAndFlush(u);
            });
        }

        if (movieReviewRepository.count() == 0) {
            for (int i = 0; i < reviews.size(); i++) {
                if(i <= 1){
                    reviews.get(i).setMovie(movies.get(0));
                    reviews.get(i).setUser(users.get(0));
                }else{
                    reviews.get(i).setMovie(movies.get(1));
                    reviews.get(i).setUser(users.get(1));
                }

                movieReviewRepository.saveAndFlush(reviews.get(i));
            }
        }
    }
}
