package com.geaden.android.movies.app.rest;

import java.util.List;

/**
 * POJO representing generic response from TMDB Api.
 *
 * @author Gennady Denisov
 */
public class TmdbResponse<Item> {
    private List<Item> results;

    public List<Item> getResults() {
        return results;
    }
}
