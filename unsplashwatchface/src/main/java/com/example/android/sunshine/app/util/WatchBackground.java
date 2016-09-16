package com.example.android.sunshine.app.util;

/**
 * Created by AArifi on 9/12/2016
 * Project:Unsplash
 * Email "adonisarifi@gmail.com"
 */

public enum WatchBackground {

    BLUE("#2196F3"), BLACK("#ffffff"), READ("#F44336"), ORANGE("#FF9800"),
    GREY("#9E9E9E"), GREEN("#4CAF50"), CYAN_400("#00BCD4"), PINK("#E91E63"), TEAL("#009688"), BROWN("#795548"), INDIGO("#3F51B5");

    public WatchBackground getNext() {
        return values()[(ordinal() + 1) % values().length];

    }

    private String color;

    private WatchBackground(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
