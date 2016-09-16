package com.example.android.sunshine.app.unsplash.api;

import android.media.Image;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by AArifi on 9/7/2016.
 */

public interface IUnsplashEndpoint {

    @GET("/")
    Observable<Image> getUnsplashResponsObservable();
}
