package com.example.sofia.booklisting;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Sofia on 10/7/2017.
 */

public class Book {
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private Bitmap imageThumb;
    private String url;


    public Book(String title, List<String> authors, String publisher, String publishedDate, String description, Bitmap imageThumb, String url) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.imageThumb = imageThumb;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImageThumb() {
        return imageThumb;
    }

    public String getUrl() {
        return url;
    }
}
