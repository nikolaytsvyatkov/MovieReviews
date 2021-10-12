package com.fmi.reviews.web.mvc;

import com.fmi.reviews.model.Movie;
import com.fmi.reviews.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Controller
@Slf4j
public class MovieMvcController {

    MovieService movieService;

    private static final String UPLOADS_DIR = "uploads";

    @Autowired
    public MovieMvcController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies")
    String getMovies(Model model){
        model.addAttribute("movies", movieService.getMovies());
        return "movies";
    }

    @GetMapping("/movie-form")
    String getMovieForm(@ModelAttribute("movie") Movie movie,
                        Model model,
                        @RequestParam(value="mode", required=false) String mode,
                        @RequestParam(value="movieId", required=false) Long movieId){
        String title = "Add movie";
        if("edit".equals(mode)){
            title = "Edit movie";
            Movie edit = movieService.getMovie(movieId);
            model.addAttribute("movie", edit);
        }

        model.addAttribute("title", title);
        return "movie-form";
    }

    @PostMapping(value = "/movies", params = "delete")
    String deleteMovie(@RequestParam("delete") Long deleteId){
        Movie old = movieService.deleteMovie(deleteId);
        handleFile(null, old);
        return "redirect:/movies";
    }

    @PostMapping(value = "/movies", params = "edit")
    String editMovie(@RequestParam("edit") Long editId,
                     UriComponentsBuilder uriBuilder){
        URI uri = uriBuilder.path("/movie-form")
                .query("mode=edit&movieId={id}").buildAndExpand(editId).toUri();
        return "redirect:" + uri.toString();
    }

    @PostMapping(value = "/movies", params = "movieId")
    String openReviews(Model model,
                       @RequestParam("movieId") Long editId,
                       UriComponentsBuilder uriBuilder){
        URI uri = uriBuilder.path("/reviews")
                .query("movieId={id}").buildAndExpand(editId).toUri();
        return "redirect:" + uri.toString();
    }

    @PostMapping("/movie-form")
    String addMovie(@Valid @ModelAttribute("movie") Movie movie,
                    BindingResult errors,
                    @RequestParam("file") MultipartFile file,
                    Model model){
        if(errors.hasErrors()){
            model.addAttribute("fileError", null);
            return "movie-form";
        }

        if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
            if (Pattern.matches("\\w+\\.(jpg|png)", file.getOriginalFilename())) {
                handleFile(file, movie);
            } else {
                model.addAttribute("fileError", "Submit picture [.jpg, .png]");
                return "movie-form";
            }
        }

        if(movie.getId() == null){
            movieService.addMovie(movie);
        }else{
            movieService.updateMovie(movie.getId(), movie);
        }

        return "redirect:/movies";
    }

    private void handleFile(MultipartFile file, Movie recipe) {
        String oldName = recipe.getMoviePhoto();
        if (oldName != null && oldName.length() > 0) { //delete old image file
            Path oldPath = Paths.get(getUploadsDir(), oldName).toAbsolutePath();
            if (Files.exists(oldPath)) {
                try {
                    Files.delete(oldPath);
                } catch (IOException ex) {
                }
            }
        }
        if (file != null && file.getOriginalFilename().length() > 4) {
            String newName = file.getOriginalFilename();
            Path newPath = Paths.get(getUploadsDir(), newName).toAbsolutePath();
            int n = 0;
            String finalName = newName;
            while (Files.exists(newPath)) {   // change destination file name if it already exists
                finalName = newName.substring(0, newName.length() - 4) + "_" + ++n + newName.substring(newName.length() - 4);
                newPath = Paths.get(getUploadsDir(), finalName).toAbsolutePath();
            }
            try {
                FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(newPath));
                recipe.setMoviePhoto(finalName);
            } catch (IOException ex) {
            }
        }
    }

    protected String getUploadsDir() {
        File uploadsDir = new File(UPLOADS_DIR);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
        return uploadsDir.getAbsolutePath();
    }
}
