package com.example.android.sunshine.app.watchview;

import android.content.Context;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by AArifi on 9/10/2016
 * Project:Unsplash
 * Email "adonisarifi@gmail.com"
 */

public class UnsplashPresenter implements UnsplashContract.SunshinePresenter {
    private Context context;
    private CompositeSubscription mSubscriptions;

    public UnsplashPresenter(Context context) {
        this.context = context;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void updateBackground() {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
