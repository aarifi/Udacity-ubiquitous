package com.example.android.sunshine.app.unsplash.api;

import android.content.Context;
import android.media.Image;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by AArifi on 9/7/2016.
 */

public class UnsplashApi {

    private final Context context;
    private static UnsplashApi unsplashApiInstance;
    private IUnsplashEndpoint iUnsplashEndpoint;

    public UnsplashApi(Context context) {
        this.context = context;
    }

    public static UnsplashApi getUnsplashApiInstance(Context context) {
        if (unsplashApiInstance == null) {
            unsplashApiInstance = new UnsplashApi(context);
        }

        return unsplashApiInstance;
    }

    public IUnsplashEndpoint getIUnsplashEndpoint(String base_url) {
        final OkHttpClient okHttpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iUnsplashEndpoint = retrofit.create(IUnsplashEndpoint.class);

        return iUnsplashEndpoint;
    }

    public Observable<Image> getStringObservable() {
        return getIUnsplashEndpoint("https://source.unsplash.com/user/erondu/daily").getUnsplashResponsObservable();
    }
}
