package com.geaden.android.movies.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database contract for the movie.
 *
 * @author Gennady Denisov
 */
public class MovieContract {
    // The "Content Authority" is a name for the entire content provider, similar to the
    // relationships between a domain name and its website. A convention string to use for
    // the content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "com.geaden.android.movies.app";

    // User CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITES = "favorites";

    /* Inner class that defines the table contents of the movie table */
    public static class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +  CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";
        /** List of available columns **/
        // Id of the movie from backend side (i.e. if user wants to rate the movie)
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }

    /* Inner class that defines the tables contents of movie_favourite table */
    public static class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +  CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";
        // Id of the movie from TMDB api
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // When the movie was favored
        public static final String COLUMN_FAVORED_AT = "favored_at";

        public static Uri buildFavoriteUri(long favId) {
            return ContentUris.withAppendedId(CONTENT_URI, favId);
        }
    }

    // TODO: What about genres?
}
