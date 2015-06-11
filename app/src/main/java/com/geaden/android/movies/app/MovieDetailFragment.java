package com.geaden.android.movies.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.geaden.android.movies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Movie detail fragment.
 *
 * @author Gennady Denisov
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String MOVIE_DETAIL_ID = "movie_detail_id";

    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private RatingBar mMovieRating;

    private final int MOVIE_DETAIL_LOADER = 0;

    private long mMovieId;

    public MovieDetailFragment() {

    }

    public static Fragment getInstance(long movieId) {
        Fragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putLong(MOVIE_DETAIL_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle args = getArguments();
        if (args.containsKey(MOVIE_DETAIL_ID)) {
            mMovieId = args.getLong(MOVIE_DETAIL_ID);
        }
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_detail_title);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        mMovieRating = (RatingBar) rootView.findViewById(R.id.movie_detail_rating);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.buidlMovieUri(mMovieId),
                MovieGridFragment.MOVIE_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data && data.moveToFirst()) {
            String title = data.getString(MovieGridFragment.TITLE);
            float voteAvg = data.getFloat(MovieGridFragment.VOTE_AVG);
            String overview = data.getString(MovieGridFragment.OVERVIEW);
            String posterPath = data.getString(MovieGridFragment.POSTER_PATH);
            mMovieTitle.setText(title);
            mMovieOverview.setText(overview);
            Picasso.with(getActivity()).load(posterPath).into(mMoviePoster);
            mMovieRating.setRating(Utility.getRating(getActivity(), voteAvg));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
