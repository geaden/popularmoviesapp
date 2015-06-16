package com.geaden.android.movies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.geaden.android.movies.app.sync.MovieSyncAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Holds some utility methods.
 *
 * @author Gennady Denisov
 */
public class Utility {
    /**
     * Gets preferred sort order from user settings
     * @param c the context to get SharedPreferences from
     * @return the preferred sort order
     */
    static public String getPreferredSortOrder(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.pref_sort_key),
                c.getString(R.string.pref_default_sort_order_value));
    }

    /**
     * Formats a data according to format
     * @param date the date to format. If null, then null is returned.
     * @param format the provided format
     * @return string representation of date according to the provided format
     */
    static public String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * Gets the connection status
     * @param context the Context to get Preferences from
     * @return the connection status
     */
    @SuppressWarnings("ResourceType")
    static public @MovieSyncAdapter.ConnectionStatus int getConnectionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_conn_status_key),
                MovieSyncAdapter.CONNECTION_UNKOWN);
    }

    /**
     * Returns true if the network is available or about to become available
     *
     * @param c Context used to the ConnectivityManager
     * @return
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /**
     * Gets movie rating according to max allowed stars
     * @param c the Context to get max stars from
     * @param voteAvg average vote
     * @return normalized rating
     */
    public static float getRating(Context c, float voteAvg) {
        int maxVote = 10;
        int maxStars = c.getResources().getInteger(R.integer.movie_rating_stars);
        return voteAvg * maxStars / maxVote;
    }

    /**
     * Gets movie release date from provided date string
     * @param dateStr the date string
     * @return year as string
     */
    public static String getReleaseYear(String dateStr) {
        if (dateStr != null) {
            return dateStr.split("-")[0];
        }
        return null;
    }
}
