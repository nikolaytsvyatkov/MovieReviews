package com.fmi.reviews.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank(message = "Title must not be null")
    private String movieTitle;

    @NonNull
    @NotBlank(message = "Description must not be null")
    private String description;

    private String moviePhoto;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate = LocalDate.now();

    @OneToMany(mappedBy = "movie", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<MovieReview> reviews = new HashSet<>();

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created = LocalDateTime.now();

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modified = LocalDateTime.now();

    public Movie(@NonNull @NotNull String movieTitle, @NonNull @NotNull String description, Set<MovieReview> reviews) {
        this.movieTitle = movieTitle;
        this.description = description;
        this.reviews = reviews;
    }
}
