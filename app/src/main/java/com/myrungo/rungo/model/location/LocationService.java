package com.myrungo.rungo.model.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.myrungo.rungo.AppActivity;
import com.myrungo.rungo.R;
import com.myrungo.rungo.Scopes;
import com.myrungo.rungo.model.SchedulersProvider;
import com.myrungo.rungo.model.database.AppDatabase;
import com.myrungo.rungo.model.database.entity.LocationDb;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import timber.log.Timber;
import toothpick.Toothpick;

public class LocationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_1";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 0;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private NotificationManager mNotificationManager;
    private Double distanceInMeters = 0.0;

    @Inject
    AppDatabase database;
    @Inject
    TraininigListener trainingListener;
    @Inject
    SchedulersProvider schedulers;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LocationService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP));

        createNotificationChannel();

        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (isLocationPermissionsGranted()) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

        startForeground(NOTIFICATION_ID, getNotification());
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @NonNull final CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            @NonNull final NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private boolean isLocationPermissionsGranted() {
        final boolean fineLocationPermissionGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        final boolean coarseLocationPermissionGranted =
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        return fineLocationPermissionGranted && coarseLocationPermissionGranted;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        super.onDestroy();
    }

    private void onNewLocation(final Location newLocation) {
        if ((currentLocation == null) && (newLocation.getAccuracy()<10)){
            currentLocation = newLocation;
            return;
        }

        if (!trainingListener.isRun()) {
            return;
        }
        if(currentLocation!=null) {
            if (isLocationAccuracy(newLocation)) {
                distanceInMeters += newLocation.distanceTo(currentLocation);
                currentLocation = newLocation;

                @NonNull final Disposable task = Completable
                        .fromAction(new Action() {
                            @Override
                            public void run() {
                                @NonNull final LocationDb locationDb = new LocationDb(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude(),
                                        0
                                );

                                database.getLocationDao().insert(locationDb);
                            }
                        })
                        .subscribeOn(schedulers.io())
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull final Throwable throwable) {
                                Timber.e(throwable);
                            }
                        })
                        .subscribe();

                compositeDisposable.add(task);
                mNotificationManager.notify(NOTIFICATION_ID, getNotification());
            }
        }
        trainingListener.send(distanceInMeters,newLocation.getSpeed(), newLocation.getAccuracy());
    }

    private boolean isLocationAccuracy(@NonNull final Location newLocation) {
        return newLocation.distanceTo(currentLocation) > newLocation.getAccuracy() + currentLocation.getAccuracy();
    }

    @NonNull
    private Notification getNotification() {
        @NonNull final Intent intent = new Intent(this, AppActivity.class);
        @NonNull final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        @NonNull final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(String.valueOf(distanceInMeters.intValue()) + " " + getString(R.string.meter))
                .setOngoing(true)
                .setVibrate(new long[] {0L})
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_cat)
                .setShowWhen(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }
}
