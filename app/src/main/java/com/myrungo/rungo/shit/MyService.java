package com.myrungo.rungo.shit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.myrungo.rungo.R;
import com.myrungo.rungo.shit.start.StartActivity;

public final class MyService extends Service implements LocationListener, GpsStatus.Listener {

    @Nullable
    private LocationManager locationManager;

    @Nullable
    private Location lastlocation;

    @Nullable
    private Data data;

    private double currentLon = 0;
    private double currentLat = 0;
    private double lastLon = 0;
    private double lastLat = 0;

    @Nullable
    private PendingIntent contentIntent;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        @NonNull final Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        updateNotification(false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull final Location location) {
        data = StartActivity.getData();

        if (data != null && data.isRunning()) {
                /*lastlocation.setLatitude(lastLat);
                lastlocation.setLongitude(lastLon);
                double distance = lastlocation.distanceTo(location);

                if (location.getAccuracy() < distance){
                    data.addDistance(distance);
                    data.addPosition(new LatLng(currentLat, currentLon));
                    lastLat = currentLat;
                    lastLon = currentLon;
                }*/
            if (lastlocation == null) {
                lastlocation = location;
            } else {
                if (location.getAccuracy() + lastlocation.getAccuracy() < location.distanceTo(lastlocation)) {
                    data.addDistance(location.distanceTo(lastlocation));
                    data.addPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    lastlocation = location;
                }
            }

            if (location.hasSpeed()) {
                data.setCurSpeed(location.getSpeed() * 3.6);
                if (location.getSpeed() == 0) {
                    new IsStillStopped().execute();
                }
            }

            data.update();
            updateNotification(true);
        }
    }

    private void updateNotification(final boolean asData) {
        @NonNull final Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.running))
                .setSmallIcon(R.drawable.ic_cat)
                .setContentIntent(contentIntent);

        if (asData) {
            @NonNull final String text =
                    String.format(
                            getString(R.string.notification),
                            Math.round(data.getMaxSpeed()),
                            Math.round(data.getDistance())
                    );
            builder.setContentText(text);
        } else {
            builder.setContentText(String.format(getString(R.string.notification), '-', '-'));
        }

        @NonNull final Notification notification = builder.build();

        startForeground(R.string.noti_id, notification);
    }

    @Override
    public int onStartCommand(@Nullable final Intent intent, final int flags, final int startId) {
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    @Nullable
    public IBinder onBind(@NonNull final Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
        stopForeground(true);
    }

    @Override
    public void onGpsStatusChanged(final int event) {
    }

    @Override
    public void onProviderDisabled(@NonNull final String provider) {
    }

    @Override
    public void onProviderEnabled(@NonNull final String provider) {
    }

    @Override
    public void onStatusChanged(
            @NonNull final String provider,
            final int status,
            @Nullable final Bundle extras) {
    }

    class IsStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;

        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (data.getCurSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (@NonNull final InterruptedException t) {
                return "The sleep operation failed";
            }

            return "return object when task is finished";
        }

        @Override
        protected void onPostExecute(@Nullable final String message) {
            data.setTimeStopped(timer);
        }

    }

}
