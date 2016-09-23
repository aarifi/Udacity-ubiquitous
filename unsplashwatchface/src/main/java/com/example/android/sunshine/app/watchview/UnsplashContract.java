package com.example.android.sunshine.app.watchview;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by AArifi on 9/10/2016
 * Project:Unsplash
 * Email "adonisarifi@gmail.com"
 */

public interface UnsplashContract {
    interface View extends BaseView<UnsplashContract.SunshinePresenter> {

        void setupView(android.view.View v);

        void initializeControls(android.view.View view);

        void connectToWearableApiGoogleApi();

        Bitmap getImageForLevelOfBattery(String levelOfBattery);

        void updateTimer();

        void setDateAndTime();

        void requestWeatherInfoFromPhone();

        void updateLayoutOnAmbientMode(Canvas canvas);

        void setBackground();

        void setWeatherData(String highTem, String lowTem);

        void setLocationName(String locationName);

        void setSteps();

        void setBateryPercentage();

        void remindmetobringumbrell();

        void showNearbyWatch();

        void registerReceiver();

        void unregisterReceiver();


    }

    interface SunshinePresenter extends BasePresenter {

        void updateBackground();
    }
}
