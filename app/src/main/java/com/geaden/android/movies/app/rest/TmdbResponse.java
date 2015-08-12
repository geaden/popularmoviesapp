package com.geaden.android.movies.app.rest;

import java.util.List;

/**
 * POJO representing generic response from TMDB Api.
 *
 * @author Gennady Denisov
 */
public class TmdbResponse<TmdbEntity> {
    private List<TmdbEntity> results;

    public List<TmdbEntity> getResults() {
        return results;
    }
}
