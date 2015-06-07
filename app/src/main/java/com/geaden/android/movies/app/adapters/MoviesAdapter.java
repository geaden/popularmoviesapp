package com.geaden.android.movies.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.geaden.android.movies.app.R;
import com.geaden.android.movies.app.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Special adapter to show movies.
 *
 * @author Gennady Denisov
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {
    private int layout;

    public MoviesAdapter(Context context, int resource, List<Movie> movies) {
        super(context, resource, movies);
        layout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(getContext()).load(movie.getPosterPath()).into(holder.poster);
        holder.poster.setContentDescription(movie.getTitle());
        holder.title.setText(movie.getTitle());
        holder.overview.setText(movie.getOverview());
        holder.rating.setRating(movie.getRating(getContext()));
        return convertView;
    }

    private class ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;
        RatingBar rating;

        ViewHolder(View v) {
            poster = (ImageView) v.findViewById(R.id.movie_poster);
            title = (TextView) v.findViewById(R.id.movie_title);
            overview = (TextView) v.findViewById(R.id.movie_overview);
            rating = (RatingBar) v.findViewById(R.id.movie_rating);
        }
    }
}
