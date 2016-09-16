package com.example.android.sunshine.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.example.android.sunshine.app.R;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by AdonisArifi on 27.7.2016 - 2016 . PGoCaptureMoment
 */

public class SupportMethod {

    public static SupportMethod supportMethodInstance;
    private Context myContext;

    public SupportMethod(Context context) {
        myContext = context;
    }

    public static SupportMethod getSupportMethodInstance(Context context) {
        if (supportMethodInstance == null) {
            supportMethodInstance = new SupportMethod(context);
        }
        return supportMethodInstance;
    }


    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String convertTimeFromUtctoMyLocal(String timeFromServer) {
        String dateTimeLocal = "";
        try {

            //get timeZone  as a number (+02)for loca date
            DateTimeZone tz = DateTimeZone.getDefault();
            Long instant = DateTime.now().getMillis();

            String name = tz.getName(instant);

            long offsetInMilliseconds = tz.getOffset(instant);
            long hours = TimeUnit.MILLISECONDS.toHours(offsetInMilliseconds);
            String offset = Long.toString(hours);


            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

            DateTime utcTime = new DateTime(dateTimeFormatter.parseDateTime(timeFromServer));
            DateTimeZone dateTimeZone = DateTimeZone.getDefault();
            int timeZone = dateTimeZone.getOffset(new DateTime());
            DateTime finalyTime;
            if (offset.contains("-")) {
                finalyTime = utcTime.withZone(DateTimeZone.getDefault()).minusHours(Integer.parseInt(offset));
            } else {
                finalyTime = utcTime.withZone(DateTimeZone.getDefault()).plusHours(Integer.parseInt(offset));

            }

            dateTimeLocal = finalyTime.toString(dateTimeFormatter);
        } catch (Exception e) {
            e.getMessage();
        }

        return dateTimeLocal;
    }

    public static String getCurrentTimeStampOnUTC() {
        String dateTimeUtc = null;
        try {
            // get current moment in default time zone
            DateTime dt = new DateTime();

            // translate to UTC local time
            DateTime dateTimeWithZoneUtc = dt.withZone(DateTimeZone.UTC);

            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

            dateTimeUtc = dateTimeWithZoneUtc.toString(dateTimeFormatter);
        } catch (Exception e) {
            e.getMessage();
        }

        return dateTimeUtc;
    }


    public static Date getConvertTimeStampToDate(String stringDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date currentTimeStamp = dateFormat.parse(stringDate);

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long getCurrentTime = getConvertTimeStampToDate(getCurrentTimeStamp()).getTime();
        long now = getCurrentTime;
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static int getIconForWeather(int weatherId) {

        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }
}
