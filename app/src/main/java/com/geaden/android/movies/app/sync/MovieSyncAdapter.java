package com.geaden.android.movies.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import com.geaden.android.movies.app.R;
import com.geaden.android.movies.app.Utility;
import com.geaden.android.movies.app.data.MovieContract;
import com.geaden.android.movies.app.data.MovieContract.MovieEntry;
import com.geaden.android.movies.app.data.MovieContract.FavoriteEntry;
import com.geaden.android.movies.app.data.MovieContract.TrailerEntry;
import com.geaden.android.movies.app.data.MovieContract.ReviewEntry;
import com.geaden.android.movies.app.models.Movie;
import com.geaden.android.movies.app.models.Review;
import com.geaden.android.movies.app.models.Trailer;
import com.geaden.android.movies.app.rest.RestClient;
import com.geaden.android.movies.app.rest.UnauthorizedException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Movie sync adapter to allow periodic syncs.
 *
 * @author Gennady Denisov
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public static final int CONNECTION_OK = 0;
    public static final int CONNECTION_SERVER_DOWN = 1;
    public static final int CONNECTION_SERVER_INVALID = 2;
    public static final int CONNECTION_UNKNOWN = 3;
    public static final int CONNECTION_SYNC = 4;

    // Interval at which to sync with the TMDB, in seconds.
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTION_OK,
            CONNECTION_SERVER_DOWN,
            CONNECTION_SERVER_INVALID,
            CONNECTION_UNKNOWN,
            CONNECTION_SYNC})
    public @interface ConnectionStatus {}

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        setConnectionStatus(getContext(), CONNECTION_SYNC);
        String sortOrder = Utility.getPreferredSortOrder(getContext());
        try {
            List<Movie> movieList = RestClient.getsInstance().queryMovies(sortOrder);
            addMovies(movieList);
        } catch (UnauthorizedException e) {
            Log.e(LOG_TAG, "Error retrieving movies", e);
            setConnectionStatus(getContext(), CONNECTION_UNKNOWN);
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Unknown server error", e);
            setConnectionStatus(getContext(), CONNECTION_UNKNOWN);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context An app context
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
         /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(getSyncAccount(context),
                context.getResources().getString(R.string.content_authority),
                bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the account doesn't exist yet.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type)
        );

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
            // Add the account and account type, no password or user data
            // If successful, return the Account object, otherwise report an error
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            // If you don't set android:syncable="true" in
            // your <provider> element in the manifest,
            // then call context.setIsSyncable(account, AUTHORITY, 1)
            // here.
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        // Schedule the sync for periodic execution
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        // Without calling setSyncAutomatically, our periodic sync will not be enabled.
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);

        // Let's do a sync to get things started.
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to store list of movies in database
     * @param movieList the list of movies
     */
    private void addMovies(final List<Movie> movieList) {
        // Insert the new movies data into the database
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
            Cursor favoredCursor = getContext().getContentResolver().query(
                    FavoriteEntry.CONTENT_URI,
                    new String[]{FavoriteEntry.COLUMN_MOVIE_ID},
                    null,
                    null,
                    null);
            List<String> favored = new ArrayList<String>(favoredCursor.getCount());
            List<String> parameters = new ArrayList<String>(favoredCursor.getCount());
            while (favoredCursor.moveToNext()) {
                favored.add(favoredCursor.getString(favoredCursor.getColumnIndex(
                        FavoriteEntry.COLUMN_MOVIE_ID)));
                parameters.add("?");
            }
            // We're done. Now we have a list of favored movies.
            favoredCursor.close();
            Log.d(LOG_TAG, "Favored movies: " + TextUtils.join(", ", favored));
            // Delete all movies except favored ones
            getContext().getContentResolver().delete(MovieEntry.CONTENT_URI,
                    MovieEntry.COLUMN_MOVIE_ID + " NOT IN (" + TextUtils.join(", ", parameters) +
                            ")", favored.toArray(new String[favored.size()]));
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " inserted.");
        setConnectionStatus(getContext(), CONNECTION_OK);
        // Sync trailers and reviews
        // Due to API restrictions (40 requests at the time)
        // no way to perform this in parallel
        addTrailers(movieList);
        addReviews(movieList);
    }

    /**
     * Retrieves trailers and store in internal database
     * @param movieList the list of available movies
     */
    private void addTrailers(List<Movie> movieList) {
        for (Movie movie : movieList) {
            try {
                List<Trailer> trailers = RestClient.getsInstance().queryTrailers(movie.getId());
                Vector<ContentValues> cVVector = new Vector<ContentValues>();
                for (Trailer trailer : trailers) {
                    ContentValues cv = new ContentValues();
                    cv.put(TrailerEntry.COLUMN_MOVIE_ID, movie.getId());
                    cv.put(TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
                    cv.put(TrailerEntry.COLUMN_NAME, trailer.getName());
                    cv.put(TrailerEntry.COLUMN_KEY, trailer.getKey());
                    cv.put(TrailerEntry.COLUMN_ISO_639_1, trailer.getIso6391());
                    cv.put(TrailerEntry.COLUMN_SIZE, trailer.getSize());
                    cv.put(TrailerEntry.COLUMN_SITE, trailer.getSite());
                    cv.put(TrailerEntry.COLUMN_TYPE, trailer.getType());
                    cVVector.add(cv);
                }

                // Add trailers to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "Sync Trailers Complete. " + cVVector.size() + " inserted.");
                setConnectionStatus(getContext(), CONNECTION_OK);
            } catch (Throwable e) {
                setConnectionStatus(getContext(), CONNECTION_SERVER_INVALID);
            }
        }
    }

    /**
     * Retrieves trailers and store in internal database
     * @param movieList the list of available movies
     */
    private void addReviews(List<Movie> movieList) {
        for (Movie movie : movieList) {
            try {
                List<Review> reviews = RestClient.getsInstance().queryReviews(movie.getId());
                Vector<ContentValues> cVVector = new Vector<ContentValues>();
                for (Review review : reviews) {
                    ContentValues cv = new ContentValues();
                    cv.put(ReviewEntry.COLUMN_MOVIE_ID, movie.getId());
                    cv.put(ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                    cv.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                    cv.put(ReviewEntry.COLUMN_CONTENT, review.getContent());
                    cv.put(ReviewEntry.COLUMN_URL, review.getUrl());
                    cVVector.add(cv);
                }

                // Add reviews to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "Sync Reviews Complete. " + cVVector.size() + " inserted.");
                setConnectionStatus(getContext(), CONNECTION_OK);
            } catch (Throwable e) {
                setConnectionStatus(getContext(), CONNECTION_SERVER_INVALID);
            }
        }
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
