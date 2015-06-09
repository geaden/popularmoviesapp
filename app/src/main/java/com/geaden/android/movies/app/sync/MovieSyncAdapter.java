package com.geaden.android.movies.app.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.geaden.android.movies.app.R;
import com.geaden.android.movies.app.Utility;
import com.geaden.android.movies.app.data.MovieContract.MovieEntry;
import com.geaden.android.movies.app.models.Movie;
import com.geaden.android.movies.app.rest.RestClient;
import com.geaden.android.movies.app.rest.UnauthorizedException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Vector;

/**
 * Movie sync adapter to allow periodic syncs.
 *
 * @author Gennady Denisov
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    private static final int CONNECTION_OK = 0;
    private static final int CONNECTION_SERVER_DOWN = 1;
    private static final int CONNECTION_SERVER_INVALID = 2;
    private static final int CONNECTION_UNKNOWN = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTION_OK, CONNECTION_SERVER_DOWN, CONNECTION_SERVER_INVALID, CONNECTION_UNKNOWN})
    public @interface ConnectionStatus {}

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String sortOrder = Utility.getPreferredSortOrder(getContext());
        try {
            List<Movie> movieList = RestClient.getsInstance().queryMovies(sortOrder);
            addMovies(movieList);
        } catch (UnauthorizedException e) {
            Log.e(LOG_TAG, "Error retrieving movies", e);
            setConnectionStatus(getContext(), CONNECTION_UNKNOWN);
        }
    }

    /**
     * Helper method to add list of movies
     * @param movieList the list of movies
     */
    private void addMovies(List<Movie> movieList) {
        // Insert the new movies dat into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieList.size());
        for (Movie movie : movieList) {
            ContentValues cv = new ContentValues();
            cv.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            cv.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            cv.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            cv.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            cv.put(MovieEntry.COLUMN_VOTE_AVG, movie.getVoteAvg());
            cv.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            cv.put(MovieEntry.COLUMN_LANGUAGE, movie.getOriginalLanguage());
            cv.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            cv.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            cv.put(MovieEntry.COLUMN_RELEASE_DATE,
                    Utility.formatDate(movie.getReleaseDate(), "yyyy-MM-dd"));
            cVVector.add(cv);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            // delete old data so we don't build up an endless history
            getContext().getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            // TODO: Notify user?
        }
        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " inserted");
        setConnectionStatus(getContext(), CONNECTION_OK);
    }

    /**
     * Sets the connection status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     * @param c Context to get the PreferenceManager from.
     * @param connStatus The IntDef value to set
     */
    static private void setConnectionStatus(Context c, @ConnectionStatus int connStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_conn_status_key), connStatus);
        spe.commit();
    }
}
