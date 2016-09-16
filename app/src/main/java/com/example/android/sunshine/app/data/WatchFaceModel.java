package com.example.android.sunshine.app.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AArifi on 9/10/2016
 * Project:Unsplash
 * Email "adonisarifi@gmail.com"
 */

public class WatchFaceModel implements Parcelable {

    private String weatherId;
    private String datetime;
    private int humidity;
    private double pressure;
    private double windSpeed;
    private double highTem;
    private double lowTemp;
    private String description;

    public WatchFaceModel() {

    }

    public WatchFaceModel(Parcel in) {
        weatherId = in.readString();
        datetime = in.readString();
        humidity = in.readInt();
        pressure = in.readDouble();
        windSpeed = in.readDouble();
        highTem = in.readDouble();
        lowTemp = in.readDouble();
        description = in.readString();
    }

    public static final Creator<WatchFaceModel> CREATOR = new Creator<WatchFaceModel>() {
        @Override
        public WatchFaceModel createFromParcel(Parcel in) {
            return new WatchFaceModel(in);
        }

        @Override
        public WatchFaceModel[] newArray(int size) {
            return new WatchFaceModel[size];
        }
    };

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getHighTem() {
        return highTem;
    }

    public void setHighTem(double highTem) {
        this.highTem = highTem;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(double lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(weatherId);
        parcel.writeString(datetime);
        parcel.writeInt(humidity);
        parcel.writeDouble(pressure);
        parcel.writeDouble(windSpeed);
        parcel.writeDouble(highTem);
        parcel.writeDouble(lowTemp);
        parcel.writeString(description);
    }
}
