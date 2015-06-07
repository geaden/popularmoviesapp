package com.geaden.android.movies.app.rest;

import android.util.Log;

import com.geaden.android.movies.app.models.Movie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Rest client implementation.
 *
 * @author Gennady Denisov
 */
public class RestClient {
    private final static String TMDB_API_KEY = "a1ad19ad6e16a5c7042ab107bdde5504";

    private static TmdbService sService;

    private static RestClient sInstanse;
    private static String LOG_TAG = RestClient.class.getSimpleName();

    static {
        // Date serialization with GSON: http://stackoverflow.com/questions/6873020/gson-date-format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd").create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new TmdbErrorHandler())
                .build();
        sService = restAdapter.create(TmdbService.class);
    }

    /**
     * Singleton for RestClient
     * @return the rest client
     */
    public static RestClient getsInstance() {
        if (sInstanse == null) {
            sInstanse = new RestClient();
        }
        return sInstanse;
    }

    /**
     * Gets list of movies by calling TMDB Api
     * @return list of Movie
     */
    public List<Movie> queryMovies() {
        try {
            MovieResponse resp = sService.queryMovies("popularity.desc", TMDB_API_KEY);
            return resp.getMovies();
        } catch (UnauthorizedException e) {
            Log.e(LOG_TAG, "Authorization needed", e);
        }
        return null;
    }

}
