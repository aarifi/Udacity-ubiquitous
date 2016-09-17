package com.example.android.sunshine.app.watchview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.util.Constants;
import com.example.android.sunshine.app.util.SupportMethod;
import com.example.android.sunshine.app.util.WatchBackground;
import com.example.android.sunshine.app.util.WatchFaceSharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by AArifi on 9/10/2016
 * Project:Unsplash
 * Email "adonisarifi@gmail.com"
 */

public class SunshineWatchFace extends CanvasWatchFaceService {

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;
    private static final String TAG = SunshineWatchFace.class.getSimpleName();

    @Override
    public SunshineWatchFace.Engine onCreateEngine() {
        return new WatchFaceEngine();
    }

    private class WatchFaceEngine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
            UnsplashContract.View {

        /**
         * Handler to update the time periodically in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };
        private final Point displaySize = new Point();


        //region declare variable
        boolean mRegisteredTimeZoneReceiver = false;
        GoogleApiClient mGoogleApiClient;
        TextView textView_time;
        TextView textView_day_of_week;
        TextView textView_day_of_month;
        TextView textView_max_temp;
        TextView textView_min_temp;
        TextView textView_battery_percentage;
        TextView textView_location;
        ImageView imageView_weather;
        RelativeLayout relativeLayout_root;
        float mXOffset = 0;
        float mYOffset = 0;
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;
        boolean mAmbient;
        WatchBackground a = WatchBackground.BLUE;
        private View myLayout;
        private int specW, specH;
        private String mHighTemp;
        private String mLowTemp;
        private String locationName;
        private Bitmap weather_icon;
        private DateTime dateTime;

        //endregion

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: " + intent.getStringExtra("time-zone"));
                dateTime = new DateTime();
            }
        };

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(SunshineWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            dateTime = new DateTime();


            // Inflate the layout that we're using for the watch face
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myLayout = inflater.inflate(R.layout.round_activity_wear, null);

            // Load the display spec - we'll need this later for measuring myLayout
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);
            setupView(myLayout);
            Log.d(TAG, "onCreate: ");
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mGoogleApiClient.connect();
                registerReceiver();

            } else {
                unregisterReceiver();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            Log.d(TAG, "onApplyWindowInsets: ");
            super.onApplyWindowInsets(insets);
            if (insets.isRound()) {
                // Shrink the face to fit on a round screen
               /* mYOffset = mXOffset = displaySize.x * 0.1f;
                displaySize.y -= 2 * mXOffset;
                displaySize.x -= 2 * mXOffset;*/

            } else {
                mXOffset = mYOffset = 0;
                textView_time.setTextSize(45f);
            }

            // Recompute the MeasureSpec fields - these determine the actual size of the layout
            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.EXACTLY);

        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                invalidate();
            }


            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            setDateAndTime();
            setWeatherData(mHighTemp, mLowTemp);
            setBateryPercentage();
            updateLayoutOnAmbientMode(canvas);
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "Connected to Google Play...");
            Wearable.DataApi.addListener(mGoogleApiClient, SunshineWatchFace.WatchFaceEngine.this);
            requestWeatherInfoFromPhone();

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "Disconnected from Google Play...");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "Google Play connection failed...");
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d(TAG, "onDataChanged: ");
            for (DataEvent dataEvent : dataEvents) {
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    String path = dataEvent.getDataItem().getUri().getPath();
                    Log.d(TAG, path);
                    if (path.equals(Constants.WEATHER_INFO_PATH)) {
                        mHighTemp = dataMap.getString(Constants.KEY_HIGH);
                        mLowTemp = dataMap.getString(Constants.KEY_LOW);
                        locationName = dataMap.getString(Constants.KEY_LOCATION);
                        if (dataMap.containsKey(Constants.KEY_WEATHER_ID)) {
                            int weatherId = dataMap.getInt(Constants.KEY_WEATHER_ID);
                            Drawable b = getResources().getDrawable(SupportMethod.getIconForWeather(weatherId));
                            Bitmap icon = ((BitmapDrawable) b).getBitmap();
                            weather_icon = icon;

                        } else {
                            Log.d(TAG, "No weatherId?");
                        }

                        invalidate();
                    }
                    Log.d(TAG, "onDataChanged: " + mHighTemp + ":" + mLowTemp);
                }
            }
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            super.onTapCommand(tapType, x, y, eventTime);
            if (tapType == TAP_TYPE_TOUCH) {

                a = a.getNext();
                WatchFaceSharedPref.getSharedInstance(getApplicationContext()).setBackgroundColor(a.getNext().getColor());
                Log.d(TAG, "onTapCommand: " + a.getNext().getColor());
                setBackground();
            }
        }

        @Override
        public void setTouchEventsEnabled(boolean enabled) {
            super.setTouchEventsEnabled(enabled);
        }

        @Override
        public void setupView(View v) {
            connectToWearableApiGoogleApi();
            initializeControls(v);
            setLocationName("Prishtin");
            setWeatherData(mHighTemp, mLowTemp);
        }

        @Override
        public void initializeControls(View view) {
            textView_time = (TextView) view.findViewById(R.id.textView_time);
            textView_day_of_week = (TextView) view.findViewById(R.id.textView_day_of_week);
            textView_day_of_month = (TextView) view.findViewById(R.id.textView_day_of_month);
            textView_battery_percentage = (TextView) view.findViewById(R.id.textView_battery_percentage);
            relativeLayout_root = (RelativeLayout) view.findViewById(R.id.relativLayout_root);
            imageView_weather = (ImageView) view.findViewById(R.id.imageView_weather);

            textView_max_temp = (TextView) view.findViewById(R.id.textView_max_temp);
            textView_min_temp = (TextView) view.findViewById(R.id.textView_min_temp);
            textView_location = (TextView) view.findViewById(R.id.textView_location);


        }

        @Override
        public void connectToWearableApiGoogleApi() {
            mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchFace.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();
        }

        @Override
        public Bitmap getImageForLevelOfBattery(String levelOfBattery) {
            double levelOfBatt = Double.parseDouble(levelOfBattery);
            Bitmap bitmap;
            if (levelOfBatt <= 10) {

            }
            return null;
        }

        @Override
        public void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        @Override
        public void setDateAndTime() {
            try {
                dateTime = new DateTime();
                String finalyTime;
                DateTimeFormatter df = DateTimeFormat.forPattern("HH:mm");
                String time = dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour();
                finalyTime = df.print(dateTime);
                String dayOfMonth = String.valueOf(dateTime.getDayOfMonth());
                DateTime.Property dProperty = dateTime.dayOfWeek();

                textView_time.setText(finalyTime);
                textView_day_of_month.setText(dayOfMonth);
                textView_day_of_week.setText(dProperty.getAsText(Locale.getDefault()).substring(0, 3).toUpperCase());

                Log.d(TAG, "setDateAndTime: " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour() + dProperty.getAsText(Locale.getDefault()));

            } catch (Exception e) {
                e.getMessage();
            }

        }

        @Override
        public void requestWeatherInfoFromPhone() {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.WEATHER_PATH);
            putDataMapRequest.getDataMap().putString(Constants.KEY_UUID, UUID.randomUUID().toString());
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Failed request for weather data");
                            } else {
                                Log.d(TAG, "Successfully request weather data");
                            }
                        }
                    });
        }


        @Override
        public void updateLayoutOnAmbientMode(Canvas canvas) {
            // Update the layout
            myLayout.measure(specW, specH);
            setBackground();
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight());

            // Draw it to the Canvas
            canvas.drawColor(Color.BLACK);
            canvas.translate(mXOffset, mYOffset);
            myLayout.draw(canvas);
        }

        @Override
        public void setBackground() {
            if (isInAmbientMode()) {
                imageView_weather.setVisibility(View.INVISIBLE);
                relativeLayout_root.setBackgroundColor((Color.parseColor("#FF000000")));
            } else {
                relativeLayout_root.setBackgroundColor(Color.parseColor
                        (WatchFaceSharedPref.getSharedInstance(getApplicationContext()).getBackgroundColor()));
                imageView_weather.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public void setWeatherData(String hhTem, String lTem) {
            Log.d(TAG, "setWeatherData: " + hhTem + " " + lTem);
            if (mHighTemp !=null) {
                textView_max_temp.setVisibility(View.VISIBLE);
                textView_location.setVisibility(View.VISIBLE);
                imageView_weather.setVisibility(View.VISIBLE);
                textView_max_temp.setText(hhTem);
                textView_min_temp.setText(lTem);
                textView_location.setText(locationName.substring(0, 1).toUpperCase() + locationName.substring(1).toLowerCase());
                imageView_weather.setImageBitmap(weather_icon);
            }
            else {
                textView_max_temp.setVisibility(View.INVISIBLE);
                textView_location.setVisibility(View.INVISIBLE);
                imageView_weather.setVisibility(View.INVISIBLE);
            }


        }

        @Override
        public void setLocationName(String locationName) {
            Log.d(TAG, "setLocationName: " + locationName);

        }


        @Override
        public void setSteps() {

        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

        @Override
        public void setBateryPercentage() {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float) scale;

            textView_battery_percentage.setText(level + "%");
        }

        @Override
        public void registerReceiver() {

            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFace.this.registerReceiver(mTimeZoneReceiver, intentFilter);

        }

        @Override
        public void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFace.this.unregisterReceiver(mTimeZoneReceiver);

        }

        @Override
        public void setPresenter(UnsplashContract.SunshinePresenter presenter) {

        }


    }
}
