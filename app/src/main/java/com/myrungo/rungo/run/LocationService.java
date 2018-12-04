package com.myrungo.rungo.run;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.location.*;
import com.myrungo.rungo.AppActivity;
import com.myrungo.rungo.R;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    static final String ACTION_BROADCAST = "location_broadcast";
    static final String EXTRA_LOCATION = "location";
    static final String EXTRA_DISTANCE = "distance";
    static final int NOTIFICATION_ID = 1;
    static final String EXTRA_STARTED_FROM_NOTIFICATION = "started_from_noti";
    private static final String CHANNEL_ID = "channel_1";

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 0;

    private boolean mChangingConfiguration = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private NotificationManager mNotificationManager;

    private Double mDistance = 0.0;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }


    @Override
    public void onDestroy() {
        removeLocationUpdates();
        stopForeground(true);
    }

    private void onNewLocation(Location location) {

        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        //proverka na tochnost
        if(location.distanceTo(mLocation)>location.getAccuracy()+mLocation.getAccuracy()) {
            mDistance +=location.distanceTo(mLocation);
            mLocation = location;
        }
        intent.putExtra(EXTRA_DISTANCE, mDistance);
        mNotificationManager.notify(NOTIFICATION_ID,getNotification());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, RunFragment.class);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Идёт пробежка")
                .setContentText(String.valueOf(mDistance))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_cat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void removeLocationUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
        }
    }
}
