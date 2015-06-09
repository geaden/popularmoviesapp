package com.geaden.android.movies.app.models;

import android.content.Context;

import com.geaden.android.movies.app.R;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * POJO representing the movie object.
 *
 * @author Gennady Denisov
 */
public class Movie {
    private final String BASE_IMG_PATH = "http://image.tmdb.org/t/p/w185";

    private boolean adult;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private long id;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("original_title")
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("vote_average")
    private float voteAvg;
    @SerializedName("vote_count")
    private int voteCount;
    private String overview;
    private float popularity;

    public Movie() {

    }

    public boolean isAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return BASE_IMG_PATH + backdropPath;
    }

    public long getId() {
        return id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return BASE_IMG_PATH + posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public float getVoteAvg() {
        return voteAvg;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getOverview() {
        return overview;
    }

    /**
     * Gets movie rating according to max allowed stars
     * @param c the Context to get max stars from
     * @return normalized rating
     */
    public float getRating(Context c) {
        int maxVote = 10;
        int maxStars = Integer.parseInt(c.getString(R.string.movie_rating_stars));
        return voteAvg * maxStars / maxVote;
    }
}
