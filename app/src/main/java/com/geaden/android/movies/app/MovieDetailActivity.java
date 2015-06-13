package com.geaden.android.movies.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Movie detail activity.
 *
 * @author Gennady Denisov
 */
public class MovieDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            long movieId = extras.getLong(MovieDetailFragment.MOVIE_DETAIL_ID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, MovieDetailFragment.getInstance(movieId))
                    .commit();
        }
    }
}
