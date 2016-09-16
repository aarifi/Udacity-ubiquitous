package com.example.android.sunshine.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by AdonisArifi on 16.8.2016 - 2016
 */
public class WatchFaceSharedPref {


    SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesDefault;
    private Context myContext;
    public static WatchFaceSharedPref watchFaceSharedPref;

    public static WatchFaceSharedPref getSharedInstance(Context context) {
        if (watchFaceSharedPref == null) {
            watchFaceSharedPref = new WatchFaceSharedPref(context);
        }
        return watchFaceSharedPref;
    }

    public WatchFaceSharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        sharedPreferencesDefault = PreferenceManager.getDefaultSharedPreferences(context);
        myContext = context;
    }


    public void setBackgroundColor(String color) {
        Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREF_WATCH_BACKGROUND_KEY, color);
        editor.commit();
    }

    public String getBackgroundColor() {
        return sharedPreferences.getString(Constants.SHARED_PREF_WATCH_BACKGROUND_KEY, "#F44336");
    }


}
