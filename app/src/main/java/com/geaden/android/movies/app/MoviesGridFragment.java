package com.geaden.android.movies.app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geaden.android.movies.app.adapters.MoviesAdapter;
import com.geaden.android.movies.app.models.Movie;
import com.geaden.android.movies.app.rest.RestClient;

import java.util.List;


/**
 * Movies list fragment.
 */
public class MoviesGridFragment extends Fragment {

    protected GridView mMoviesGrid;

    private MoviesAdapter mMoviesAdapter;
    private String LOG_TAG = getClass().getSimpleName();

    public MoviesGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mMoviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movies_empty);
        mMoviesGrid.setEmptyView(emptyView);
        mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMoviesAdapter.getItem(position);
                Toast.makeText(getActivity(), movie.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        new QueryMoviesTask().execute();
        return rootView;
    }

    private final class QueryMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... params) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = sp.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_default_sort_order_value));
            Log.d(LOG_TAG, "Querying movies sorted by " + sortOrder);
            return RestClient.getsInstance().queryMovies(sortOrder);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            Log.d(LOG_TAG, "Adding movies to adapter...");
            mMoviesAdapter = new MoviesAdapter(getActivity(), movies);
            mMoviesGrid.setAdapter(mMoviesAdapter);
        }
    }
}
