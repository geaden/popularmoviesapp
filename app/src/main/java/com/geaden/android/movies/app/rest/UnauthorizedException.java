package com.geaden.android.movies.app.rest;

import retrofit.RetrofitError;

/**
 * Unauthorized exception for TMDB Api.
 *
 * @author Gennady Denisov
 */
public class UnauthorizedException extends Throwable {
    public UnauthorizedException(RetrofitError cause) {
        super(cause.getMessage());
    }
}
