package com.example.demo_new;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class MovieController {

    private final DBmanager dbManager = new DBmanager();

    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        return dbManager.getAllMovies();
    }


    @GetMapping("/movies/{id}")
    public Movie getMovieById(@PathVariable int id) {
        return dbManager.getAllMovies().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }
    @PostMapping("/movies")
    public String addMovie(@RequestBody Movie movie) {
        dbManager.addMovieToDB(
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getAgeRestriction(),
                movie.getTicketPrice()
        );
        return "Movie added successfully!";
    }



}