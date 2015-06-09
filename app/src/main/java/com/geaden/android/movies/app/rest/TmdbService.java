package com.geaden.android.movies.app.rest;

import com.geaden.android.movies.app.models.Movie;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Represents service to access TMDB API.
 *
 * @author Gennady Denisov
 */
public interface TmdbService {
    /**
     * Queries for the popular movies
     * @return list of Movie
     */
    @GET("/discover/movie")
    MovieResponse queryMovies(
            @Query("sort_by") String sortBy, @Query("api_key") String apiKey) throws UnauthorizedException;
}
