package com.geaden.android.movies.app.rest;

import com.geaden.android.movies.app.models.Movie;

import java.util.List;

/**
 * POJO representing response from TMDB Api when list of movies requested.
 *
 * @author Gennady Denisov
 */
public class MovieResponse {
    private int page;
    private List<Movie> results;

    public MovieResponse() {
    }

    public List<Movie> getMovies() {
        return results;
    }
}
