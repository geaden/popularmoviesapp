package com.geaden.android.movies.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Adapter to represent list of movie trailers.
 *
 * @author Gennady Denisov
 */
public class TrailersAdapter extends ArrayAdapter {
    public TrailersAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
