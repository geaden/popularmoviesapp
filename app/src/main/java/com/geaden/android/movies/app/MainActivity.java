package com.geaden.android.movies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.geaden.android.movies.app.sync.MovieSyncAdapter;


public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {
    private static final String MOVIEDETAILFRAGMENT_TAG = "movie_detail_tag";
    private Toolbar mToolbar;
    // Indicates if two pane mode should be used
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction
            if (savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        // make sure we've gotten an account created and we're syncing
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, movieUri);

            Fragment fragment = MovieDetailFragment.getInstance(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
