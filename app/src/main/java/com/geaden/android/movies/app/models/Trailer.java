package com.geaden.android.movies.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * The movie trailer pojo.
 *
 * @author Gennady Denisov
 */
public class Trailer {
    private String id;
    private String key;
    private String name;
    private String type;
    private String site;
    private int size;
    @SerializedName("iso_639_1")
    private String iso6391;
}
