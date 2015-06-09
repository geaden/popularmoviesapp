package com.geaden.android.movies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
     * @param date the date to format
     * @param format the format
     * @return string represenation of date according to format
     */
    static public String formatDate(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}
