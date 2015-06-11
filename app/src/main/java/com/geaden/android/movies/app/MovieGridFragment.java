package com.geaden.android.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.geaden.android.movies.app.adapters.MoviesAdapter;
import com.geaden.android.movies.app.data.MovieContract;
import com.geaden.android.movies.app.sync.MovieSyncAdapter;


/**
 * Movies list fragment.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected GridView mMoviesGrid;

    private final int MOVIES_LOADER = 0;

    private MoviesAdapter mMoviesAdapter;
    private String LOG_TAG = getClass().getSimpleName();

    /** Movie column projection **/
    public static final String[] MOVIE_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
    };

    /** Corresponding column indices **/
    public final static int _ID = 0;
    public final static int MOVIE_ID = 1;
    public final static int TITLE = 2;
    public final static int OVERVIEW = 3;
    public final static int POPULARITY = 4;
    public final static int VOTE_AVG = 5;
    public final static int VOTE_COUNT = 6;
    public final static int POSTER_PATH = 7;
    public final static int BACKDROP_PATH = 8;

    public MovieGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mMoviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movies_empty);
        mMoviesGrid.setEmptyView(emptyView);
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        mMoviesGrid.setAdapter(mMoviesAdapter);
        mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mMoviesAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    long movieId = cursor.getLong(_ID);
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE_DETAIL_ID, movieId);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Get the column name to order the result
        String orderCol;
        // Get the sor order
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sp.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort_order_value));
        if (sortOrder.equals(getString(R.string.pref_sort_popularity))) {
            orderCol = MovieContract.MovieEntry.COLUMN_POPULARITY;
        } else if (sortOrder.equals(getString(R.string.pref_sort_rating))) {
            orderCol = MovieContract.MovieEntry.COLUMN_VOTE_AVG;
        } else {
            orderCol = MovieContract.MovieEntry.COLUMN_POPULARITY;
        }
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_PROJECTION,
                null,
                null,
                orderCol + " DESC");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_conn_status_key))) {
            updateEmptyView();
        } else if (key.equals(getString(R.string.pref_sort_key))) {
            // Reload our data
            getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }

    /**
     * Updates the empty list view with the contextually relevant information that the user
     * can use to determine why they aren't seeing the weather.
     */
    private void updateEmptyView() {
        if (mMoviesAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.movies_empty);
            if (null != tv) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_movie_list;
                @MovieSyncAdapter.ConnectionStatus int connStatus = Utility.getConnectionStatus(getActivity());
                switch (connStatus) {
                    case MovieSyncAdapter.CONNECTION_SYNC:
                        message = R.string.empty_movie_list_syncing;
                        break;
                    case MovieSyncAdapter.CONNECTION_SERVER_DOWN:
                        message = R.string.empty_movie_list_server_down;
                        break;
                    case MovieSyncAdapter.CONNECTION_SERVER_INVALID:
                        message = R.string.empty_movie_list_list_server_error;
                        break;
                    case MovieSyncAdapter.CONNECTION_UNKOWN:
                        message = R.string.empty_movie_list_connection_unknown;
                        break;
                    case MovieSyncAdapter.CONNECTION_OK:
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_movie_list_no_network;
                        }
                }
                tv.setText(message);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "Data " + data.getCount());
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}