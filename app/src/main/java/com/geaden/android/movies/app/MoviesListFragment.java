package com.geaden.android.movies.app;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.geaden.android.movies.app.adapters.MoviesAdapter;
import com.geaden.android.movies.app.models.Movie;
import com.geaden.android.movies.app.rest.RestClient;

import java.util.List;


/**
 * Movies list fragment.
 */
public class MoviesListFragment extends Fragment {

    private ListView mMoviesList;

    private MoviesAdapter mMoviesAdapter;
    private String LOG_TAG = getClass().getSimpleName();

    public MoviesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mMoviesList = (ListView) rootView.findViewById(R.id.movies_list);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movies_empty);
        mMoviesList.setEmptyView(emptyView);
        new QueryMoviesTask().execute();
        return rootView;
    }

    private final class QueryMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Log.d(LOG_TAG, "Querying movies...");
            return RestClient.getsInstance().queryMovies();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            Log.d(LOG_TAG, "Adding movies to adapter...");
            mMoviesAdapter = new MoviesAdapter(getActivity(), R.layout.movie_list_item, movies);
            mMoviesList.setAdapter(mMoviesAdapter);
        }
    }
}
