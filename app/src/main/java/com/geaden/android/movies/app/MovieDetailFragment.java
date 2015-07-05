package com.geaden.android.movies.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.geaden.android.movies.app.adapters.ReviewsAdapter;
import com.geaden.android.movies.app.adapters.TrailersAdapter;
import com.geaden.android.movies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Movie detail fragment.
 *
 * @author Gennady Denisov
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String MOVIE_DETAIL_URI = "movie_detail_uri";
    public static String MOVIE_DETAIL_ID = "movie_detail_id";

    public final String MOVIE_SHARE_HASHTAG = "#TMDB";

    // Trailer projection
    public static final String[] TRAILER_PROJECTION = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE,
            MovieContract.TrailerEntry.COLUMN_KEY,
    };

    // Correspondent indices of trailer projection
    public static final int INDEX_TRAILER_ID = 0;
    public static final int INDEX_TRAILER_NAME = 1;
    public static final int INDEX_TRAILER_SITE = 2;
    public static final int INDEX_TRAILER_KEY = 3;

    // Review projection
    public static final String[] REVIEW_PROJECTION = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_URL
    };

    // Correspondent indices of trailer projection
    public static final int INDEX_REVIEW_ID = 0;
    public static final int INDEX_REVIEW_AUTHOR = 1;
    public static final int INDEX_REVIEW_CONTENT = 2;
    public static final int INDEX_REVIEW_URL = 3;


    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mMovieRatingNumber;
    private RatingBar mMovieRating;

    // Indicates if movie is favourite
    private boolean mFavourite = false;

    private final int MOVIE_DETAIL_LOADER = 0;
    private final int MOVIE_TRAILERS_LOADER = 1;
    private final int MOVIE_REVIEWS_LOADER = 2;

    private long mMovieId;
    private TextView mMovieReleaseDate;
    private ShareActionProvider mShareActionProvider;
    private String LOG_TAG = getClass().getSimpleName();
    private Uri mUri;
    private ListView mTrailersList;
    private ListView mReviewsList;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    public static Fragment getInstance(Bundle arguments) {
        Fragment fragment = new MovieDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private Intent createShareMovieIntent(long extMovieId) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(MOVIE_DETAIL_URI)) {
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MOVIE_DETAIL_URI);
            Log.d(LOG_TAG, "mUri " + mUri);
            mMovieId = ContentUris.parseId(mUri);
        }
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_detail_title);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        mMovieRating = (RatingBar) rootView.findViewById(R.id.movie_detail_rating);
        mMovieRatingNumber = (TextView) rootView.findViewById(R.id.movie_detail_rating_number);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
        // Trailers list
        mTrailersList = (ListView) rootView.findViewById(R.id.movie_trailers_list);
        mTrailersAdapter = new TrailersAdapter(getActivity(), null, 0);
        mTrailersList.setAdapter(mTrailersAdapter);
        TextView emptyTrailersTextView = (TextView) rootView.findViewById(R.id.trailers_empty);
        mTrailersList.setEmptyView(emptyTrailersTextView);
        mTrailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( null != mTrailersAdapter) {
                    Cursor cursor = mTrailersAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {
                        String trailerKey = cursor.getString(INDEX_TRAILER_KEY);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.getTrailerUrl(trailerKey)));
                        startActivity(intent);
                    }
                }
            }
        });
        // Reviews list
        mReviewsList = (ListView) rootView.findViewById(R.id.movie_reviews_list);
        mReviewsAdapter = new ReviewsAdapter(getActivity(), null, 0);
        mReviewsList.setAdapter(mReviewsAdapter);
        TextView emptyReviewsTextView = (TextView) rootView.findViewById(R.id.reviews_empty);
        mReviewsList.setEmptyView(emptyReviewsTextView);
        mReviewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( null != mReviewsAdapter) {
                    Cursor cursor = mReviewsAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {
                        String reviewUrl = cursor.getString(INDEX_REVIEW_URL);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrl));
                        startActivity(intent);
                    }
                }
            }
        });
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
        getActivity().getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "Movie id " + mMovieId);
        switch (id) {
            case MOVIE_DETAIL_LOADER:
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        MovieGridFragment.MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            case MOVIE_TRAILERS_LOADER:
                Log.d(LOG_TAG, "Creating trailers loader");
                return new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        TRAILER_PROJECTION,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(mMovieId)},
                        null);
            case MOVIE_REVIEWS_LOADER:
                Log.d(LOG_TAG, "Creating reviews loader");
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        REVIEW_PROJECTION,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(mMovieId)},
                        null);
            default:
                throw new UnsupportedOperationException("Unknown loader: " + id);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (!data.moveToFirst()) return;
        switch (loader.getId()) {
            case MOVIE_DETAIL_LOADER:
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
                    mShareActionProvider.setShareIntent(createShareMovieIntent(mMovieId));
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
                        mFavourite = data.getLong(MovieGridFragment.FAVORED_AT) > 0;
                        ToggleButton favouriteToggle = (ToggleButton) toolbarView.findViewById(R.id.favourite_toggle_btn);
                        favouriteToggle.setChecked(mFavourite);
                        favouriteToggle.setOnCheckedChangeListener(new FavoriteCheckedListener());
                        finishCreatingMenu(menu, data);
                    }
                }
                break;
            case MOVIE_TRAILERS_LOADER:
                Log.d(LOG_TAG, "Trailers: " + data.getCount());
                mTrailersAdapter.swapCursor(data);
                Utility.setListViewHeightBasedOnItems(mTrailersList);
                break;
            case MOVIE_REVIEWS_LOADER:
                Log.d(LOG_TAG, "Reviews: " + data.getCount());
                mReviewsAdapter.swapCursor(data);
                Utility.setListViewHeightBasedOnItems(mReviewsList);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader: " + loader.getId());
        }
    }

    /**
     * Listener on favorite checked listener
     */
    private class FavoriteCheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mFavourite = isChecked;
            if (mFavourite) {
                // Add movie to favorites
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
                getActivity().getContentResolver().insert(
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        cv);
            } else {
                // Remove movie from favorites
                getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(mMovieId)});
            }
            Toast.makeText(getActivity(), mFavourite ? getString(R.string.favorites_added) :
                            getString(R.string.favorites_removed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Finishes constructing toolbar menu, by adding share intent and modifying favourite icon
     * @param menu the toolbar menu
     * @param cursor the movie cursor
     */
    private void finishCreatingMenu(Menu menu, final Cursor cursor) {
        // Fetch and store ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        long extMovieId = cursor.getLong(MovieGridFragment.MOVIE_ID);
        setShareIntent(createShareMovieIntent(extMovieId));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == MOVIE_TRAILERS_LOADER) {
            mTrailersAdapter.swapCursor(null);
        } else if (loader.getId() == MOVIE_REVIEWS_LOADER) {
            mReviewsAdapter.swapCursor(null);
        }

    }
}
