package com.ideas.rnd.pdf.datamock;

import com.ideas.rnd.pdf.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    public static List<Movie> generate(int records) {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < records; i++) {
            Movie movie = new Movie();
            movie.setDuration("2 hr");
            movie.setMovieTitle("Robot 2.0");
            movie.setOriginalTitle("Robot");
            movie.setYear("2018");
            movie.setActor("Rajani");
            movie.setRegion("South");
            movies.add(movie);
        }
        return movies;
    }
}
