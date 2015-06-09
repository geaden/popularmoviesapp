package com.geaden.android.movies.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.geaden.android.movies.app.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Special adapter to show movies.
 *
 * @author Gennady Denisov
 */
public class MoviesAdapter extends BaseAdapter {
    private static final float IMAGE_WIDTH = 185;
    private Context mContext;
    private List<Movie> mMovies;

    public MoviesAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(movie.getPosterPath()).into(imageView);
        imageView.setContentDescription(movie.getTitle());
        return imageView;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
