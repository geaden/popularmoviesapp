package com.geaden.android.movies.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public final String MOVIE_SHARE_HASHTAG = "#TMDB";

    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mMovieRatingNumber;
    private RatingBar mMovieRating;

    private final int MOVIE_DETAIL_LOADER = 0;
    private long mMovieId;
    private TextView mMovieReleaseDate;
    private ShareActionProvider mShareActionProvider;
    private String LOG_TAG = getClass().getSimpleName();

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    public static Fragment getInstance(long movieId) {
        Fragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putLong(MOVIE_DETAIL_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    private Intent createShareMovieIntent(long extMovieId) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        Log.d(LOG_TAG, "Share " +  getString(
                R.string.movie_share_string, getMovieUrl(extMovieId), MOVIE_SHARE_HASHTAG));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(
                R.string.movie_share_string, getMovieUrl(extMovieId), MOVIE_SHARE_HASHTAG));
        return shareIntent;
    }

    /**
     * Returns movie url from provided movie id
     * @param extMovieId the external movie id
     * @return the movie url
     */
    private String getMovieUrl(long extMovieId) {
        return "https://www.themoviedb.org/movie/" + extMovieId;
    }


    /**
     * Attaches intent to share action provider
     * @param shareIntent share intent to attach
     */
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        } else {
            Log.d(LOG_TAG, "Share Action provider is null?");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);
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
        mMovieRatingNumber = (TextView) rootView.findViewById(R.id.movie_detail_rating_number);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
        LayerDrawable layerDrawable = (LayerDrawable) mMovieRating.getProgressDrawable();
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                getResources().getColor(R.color.movie_empty_star));
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                getResources().getColor(R.color.movie_partial_star));
        DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                getResources().getColor(R.color.movie_full_star));
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
        if (!data.moveToFirst()) return;

        long extMovieId = data.getLong(MovieGridFragment.MOVIE_ID);
        String title = data.getString(MovieGridFragment.TITLE);
        float voteAvg = data.getFloat(MovieGridFragment.VOTE_AVG);
        String overview = data.getString(MovieGridFragment.OVERVIEW);
        String posterPath = data.getString(MovieGridFragment.POSTER_PATH);
        String releaseDate = data.getString(MovieGridFragment.RELEASE_DATE);
        mMovieTitle.setText(title);
        mMovieOverview.setText(overview);
        Picasso.with(getActivity()).load(posterPath).into(mMoviePoster);
        mMoviePoster.setContentDescription(title);
        mMovieRating.setRating(Utility.getRating(getActivity(), voteAvg));
        mMovieRatingNumber.setText(getString(R.string.movie_rating_number, voteAvg));
        mMovieReleaseDate.setText(Utility.getReleaseYear(releaseDate));

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent(extMovieId));
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

        // We need to start the enter transition after the data has loaded
        if (activity instanceof MovieDetailActivity) {
            activity.supportStartPostponedEnterTransition();
            if ( null != toolbarView ) {
                activity.setSupportActionBar(toolbarView);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                Menu menu = toolbarView.getMenu();
                if ( null != menu ) menu.clear();
                // Locate MenuItem with ShareActionProvider
                toolbarView.inflateMenu(R.menu.menu_movie_detail);
                finishCreatingMenu(toolbarView.getMenu(), extMovieId);
            }
        }
//        else {
//            if ( null != toolbarView ) {
//                Menu menu = toolbarView.getMenu();
//                if ( null != menu ) menu.clear();
//                // Locate MenuItem with ShareActionProvider
//                toolbarView.inflateMenu(R.menu.menu_movie_detail);
//                finishCreatingMenu(toolbarView.getMenu(), extMovieId);
//            }
//        }
    }

    private void finishCreatingMenu(Menu menu, long extMovieId) {
        // Fetch and store ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent(createShareMovieIntent(extMovieId));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
