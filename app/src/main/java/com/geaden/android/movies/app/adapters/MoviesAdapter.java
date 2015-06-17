package com.geaden.android.movies.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.geaden.android.movies.app.MovieGridFragment;
import com.geaden.android.movies.app.R;
import com.squareup.picasso.Picasso;

/**
 * Special adapter to show movies.
 *
 * @author Gennady Denisov
 */
public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String posterPath = cursor.getString(MovieGridFragment.POSTER_PATH);
        String title = cursor.getString(MovieGridFragment.TITLE);
        boolean isFavourite = cursor.getInt(MovieGridFragment.IS_FAVOURITE) == 1;
        Picasso.with(context).load(posterPath).into(viewHolder.iv);
        viewHolder.iv.setContentDescription(title);
        // Show that movie is favourite
        if (isFavourite) {
            viewHolder.favouriteImageView.setVisibility(View.VISIBLE);
        }
    }

    private class ViewHolder {
        ImageView iv;
        ImageView favouriteImageView;

        ViewHolder(View v) {
            iv = (ImageView) v.findViewById(R.id.movie_poster);
            favouriteImageView = (ImageView) v.findViewById(R.id.movie_favourite);
        }
    }
}
