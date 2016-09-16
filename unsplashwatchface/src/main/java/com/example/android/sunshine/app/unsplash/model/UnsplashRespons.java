package com.example.android.sunshine.app.unsplash.model;

/**
 * Created by AArifi on 9/7/2016.
 */

public class UnsplashRespons {

    private String id;

    private String imagePath;

    private String search_tags;

    public UnsplashRespons() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch_tags() {
        return search_tags;
    }

    public void setSearch_tags(String search_tags) {
        this.search_tags = search_tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
