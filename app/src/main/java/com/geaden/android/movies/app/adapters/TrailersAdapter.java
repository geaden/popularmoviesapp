package com.geaden.android.movies.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geaden.android.movies.app.MovieDetailFragment;
import com.geaden.android.movies.app.MovieGridFragment;
import com.geaden.android.movies.app.R;
import com.squareup.picasso.Picasso;

/**
 * Adapter to represent list of movie trailers.
 *
 * @author Gennady Denisov
 */
public class TrailersAdapter extends CursorAdapter {

    public TrailersAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String trailerName = cursor.getString(MovieDetailFragment.INDEX_TRAILER_NAME);
        viewHolder.mTrailerName.setText(trailerName);
        viewHolder.mPlayButton.setContentDescription(trailerName);
    }

    private class ViewHolder {
        TextView mTrailerName;
        ImageView mPlayButton;

        ViewHolder(View view) {
            mTrailerName = (TextView) view.findViewById(R.id.movie_trailer_name);
            mPlayButton = (ImageView) view.findViewById(R.id.movie_trailer_play_button);
        }
    }
}
