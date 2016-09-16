package com.example.android.sunshine.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by AArifi on 9/4/2016.
 */

public class BackgroundModel extends RealmObject implements Parcelable {

    private int id;
    private String background_path;
    private String timestamp;
    private boolean isNight;
    private boolean isRain;
    private String sourceImage;

    public BackgroundModel() {

    }

    protected BackgroundModel(Parcel in) {
        id = in.readInt();
        background_path = in.readString();
        timestamp = in.readString();
        isNight = in.readByte() != 0;
        isRain = in.readByte() != 0;
        sourceImage = in.readString();
    }

    public static final Creator<BackgroundModel> CREATOR = new Creator<BackgroundModel>() {
        @Override
        public BackgroundModel createFromParcel(Parcel in) {
            return new BackgroundModel(in);
        }

        @Override
        public BackgroundModel[] newArray(int size) {
            return new BackgroundModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackground_path() {
        return background_path;
    }

    public void setBackground_path(String background_path) {
        this.background_path = background_path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public boolean isRain() {
        return isRain;
    }

    public void setRain(boolean rain) {
        isRain = rain;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(background_path);
        dest.writeString(timestamp);
        dest.writeByte((byte) (isNight ? 1 : 0));
        dest.writeByte((byte) (isRain ? 1 : 0));
        dest.writeString(sourceImage);
    }
}
