package com.geaden.android.movies.app.models;

/**
 * POJO representing movie review.
 *
 * @author Gennady Denisov
 */
public class Review {
    private long id;
    private String author;
    private String content;
    private String url;

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
