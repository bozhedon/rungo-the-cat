package com.myrungo.rungo.shit.start;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.myrungo.rungo.R;
import com.myrungo.rungo.cat.CatView;
import com.myrungo.rungo.shit.Data;
import com.myrungo.rungo.shit.MyService;
import com.myrungo.rungo.shit.base.BaseActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StartActivity
        extends BaseActivity<StartContract.View, StartContract.Presenter<StartContract.View>>
        implements LocationListener, GpsStatus.Listener, OnMapReadyCallback, StartContract.View {

    final static int REQUEST_LOCATION_PERMISSION_CODE_FOR_ON_RESUME = 1001;
    final static int REQUEST_APPLICATION_SETTINGS_FOR_ON_RESUME = 1003;

    @Nullable
    private LocationManager locationManager;

    @NonNull
    private LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        return locationManager;
    }

    @Nullable
    private Location startLocation;

    @Nullable
    private static Data data;

    @Nullable
    public static Data getData() {
        return data;
    }

    private void resetData() {
        getChronometer().stop();
        getDistance().setText("");
        getChronometer().setText("0");
        setData();
    }

    private void setData() {
        data = new Data(getOnGpsServiceUpdate());
    }

    @Nullable
    private Button startButton;

    @NonNull
    private Button getStartButton() {
        if (startButton == null) {
            startButton = findViewById(R.id.startButton);
        }

        return startButton;
    }

    @Nullable
    private Button stopButton;

    @NonNull
    private Button getStopButton() {
        if (stopButton == null) {
            stopButton = findViewById(R.id.stopButton);
        }

        return stopButton;
    }

    @Nullable
    private Button mapButton;

    @NonNull
    public Button getMapButton() {
        if (mapButton == null) {
            mapButton = findViewById(R.id.mapButton);
        }

        return mapButton;
    }

    @Nullable
    private TextView currentSpeed;

    @NonNull
    private TextView getCurrentSpeed() {
        if (currentSpeed == null) {
            currentSpeed = findViewById(R.id.speed);
        }

        return currentSpeed;
    }

    @Nullable
    private TextView distance;

    @NonNull
    private TextView getDistance() {
        if (distance == null) {
            distance = findViewById(R.id.distance);
        }

        return distance;
    }

    @Nullable
    private TextView avSpeed;

    @NonNull
    private TextView getAvSpeed() {
        if (avSpeed == null) {
            avSpeed = findViewById(R.id.average_speed);
        }

        return avSpeed;
    }

    @Nullable
    private TextView result;

    @Nullable
    private Chronometer chronometer;

    @NonNull
    private Chronometer getChronometer() {
        if (chronometer == null) {
            chronometer = findViewById(R.id.chronometr);
        }

        return chronometer;
    }

    @Nullable
    private Data.OnGpsServiceUpdate onGpsServiceUpdate;

    @NonNull
    private Data.OnGpsServiceUpdate getOnGpsServiceUpdate() {
        if (onGpsServiceUpdate == null) {
            onGpsServiceUpdate = new Data.OnGpsServiceUpdate() {
                @Override
                public void update() {
                    if (getData() == null) {
                        setData();
                    }

                    final double maxSpeedTemp = getData().getMaxSpeed();

                    final double averageSpeed = getData().getAverageSpeed();

                    final double averageTemp;
                    if (getSharedPreferences().getBoolean("auto_average", false)) {
                        averageTemp = getData().getAverageSpeedMotion();
                    } else {
                        averageTemp = getData().getAverageSpeed();
                    }

                    @NonNull final String distanceUnits;
                    double distanceTemp = getData().getDistance();
                    if (distanceTemp <= 1000.0) {
                        distanceUnits = " м";
                    } else {
                        distanceTemp /= 1000.0;
                        distanceUnits = " км";
                    }

                    @NonNull final String distanceString = String.valueOf(Math.round(distanceTemp)) + distanceUnits;
                    getDistance().setText(distanceString);

                    @NonNull final String speedUnits = " км/ч";
                    @NonNull final String avSpeedString = String.valueOf(Math.round(averageSpeed)) + speedUnits;
                    getAvSpeed().setText(avSpeedString);

                    if (getData().getPositions().size() > 0) {
                        @NonNull final List<LatLng> locationPoints = getData().getPositions();
                        refreshMap(getMap());
                        drawRouteOnMap(getMap(), locationPoints);
                    }
                }
            };
        }

        return onGpsServiceUpdate;
    }

    private boolean firstfix;
    private boolean first = true;
    private boolean mapActive = false;
    private int sendData = 0;

    @Nullable
    private SupportMapFragment mapFragment;

    @NonNull
    private SupportMapFragment getMapFragment() {
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        }

        //noinspection ConstantConditions
        return mapFragment;
    }

    @Nullable
    private String currentTime;

    @Nullable
    private GoogleMap map;

    @NonNull
    private GoogleMap getMap() {
        if (map == null) {
            @NonNull final RuntimeException exception = new RuntimeException("map == null");
            reportError(exception);

            throw exception;
        }

        return map;
    }

    private void setMap(@NonNull final GoogleMap map) {
        this.map = map;
    }

    @Nullable
    private CatView catView;

    @NonNull
    private CatView getCatView() {
        if (catView == null) {
            catView = findViewById(R.id.cat_view);
            catView.stop();
        }

        return catView;
    }

//    @NonNull
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    @Nullable
//    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    private StartContract.Presenter<StartContract.View> presenter;

    @Override
    protected final StartContract.Presenter<StartContract.View> getPresenter() {
        if (presenter == null) {
            @NonNull final RuntimeException exception = new RuntimeException("presenter == null");
            reportError(exception);

            throw exception;
        }

        return presenter;
    }

    @Override
    protected final void setupPresenter() {
        presenter = new StartPresenter();
    }

    @Override
    protected final void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Bundle user_bundle = getIntent().getExtras();
        //final User user;
        //user = (User) user_bundle.getSerializable(User.class.getSimpleName());
        //catView.setSkin(user.getSkin());
        //catView.setHead(user.getHead());

        getChronometer().setText("0");

        setData();

//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(new ChangeProgressEvent(sendData));
//                Intent myIntent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(myIntent);
//                dialog.cancel();
//                dialog.dismiss();
//            }
//        });

        initClickListeners();

        initMap();

        dressUp();
    }

    @Override
    protected final void onStart() {
        super.onStart();

        getCatView().resume();
        firstfix = true;

        if (data == null) {
            setData();
        }

        assert getData() != null;

        getData().setOnGpsServiceUpdate(getOnGpsServiceUpdate());

        if (!getData().isRunning()) {
            @NonNull final Gson gson = new Gson();
            //noinspection ConstantConditions
            @NonNull final String json = getSharedPreferences().getString("data", "");
            data = gson.fromJson(json, Data.class);
        }

        getPresenter().onStart();
    }

    @Override
    protected final void onStop() {
        getCatView().pause();
        super.onStop();

        getLocationManager().removeUpdates(this);
        getLocationManager().removeGpsStatusListener(this);
        @NonNull final SharedPreferences.Editor prefsEditor = getSharedPreferences().edit();
        @NonNull final Gson gson = new Gson();
        @NonNull final String json = gson.toJson(getData());
        prefsEditor.putString("data", json);
        prefsEditor.apply();
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), MyService.class));
    }

    @Override
    public final void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults
    ) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_CODE_FOR_ON_RESUME: {
                getPresenter().onRequestPermissionForOnStartResult(grantResults);
                break;
            }
        }
    }

    @Override
    public void showGoSettingsForOnStartDialog() {
        @NonNull final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull final DialogInterface dialogInterface, final int i) {
                getPresenter().onShowGoSettingsForOnStartDialogPositiveButtonClick();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull final DialogInterface dialog, final int which) {
                getPresenter().onShowGoSettingsForStartDialogNegativeButtonClick();
            }
        });


        @NonNull final String part1 =
                "Для дальнейшей работы приложения необходимо перейти в настройки приложения и " +
                        "включить разрешение доступа к местоположению.";
        @NonNull final String part2 = " Перейти?";

        @NonNull final String message = part1 + part2;

        builder.setTitle("Продолжение работы невозможно")
                .setMessage(message)
                .setCancelable(false);

        builder.create().show();
    }

    @Override
    public void showRequestPermissionRationaleForOnStart() {
        @NonNull final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull final DialogInterface dialogInterface, final int i) {
                getPresenter().onShowRequestPermissionRationaleForOnStartPositiveButtonClick();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull final DialogInterface dialog, final int which) {
                getPresenter().onShowRequestPermissionRationaleForOnStartNegativeButtonClick();
            }
        });


        @NonNull final String part1 =
                "Для установления местоположения приложению необходимы права доступа к местоположению.";
        @NonNull final String part2 = " Предоставить?";

        @NonNull final String message = part1 + part2;

        builder.setTitle("Продолжение работы невозможно")
                .setMessage(message)
                .setCancelable(false);

        builder.create().show();
    }

    @Override
    public void requestLocationPermissionForOnStart() {
        @NonNull final String[] permissions =
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_LOCATION_PERMISSION_CODE_FOR_ON_RESUME);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates() {
        if (getLocationManager().getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
            getLocationManager().requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500,
                    0,
                    this
            );
        } else {
            Toast.makeText(this, "Не удаётся подключиться к GPS", Toast.LENGTH_SHORT).show();
        }

        if (!getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }

        getLocationManager().addGpsStatusListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(@NonNull final Location location) {
        if (location.hasAccuracy()) {
            @NonNull final String text = String.format("%.0f", location.getAccuracy()) + "м";
            @NonNull final SpannableString spannableString = new SpannableString(text);

            spannableString
                    .setSpan(
                            new RelativeSizeSpan(0.75f),
                            spannableString.length() - 1,
                            spannableString.length(),
                            0
                    );

            if (firstfix) {
                firstfix = false;
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            @NonNull final String speed =
                    String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + " км/ч";

            @NonNull final SpannableString s = new SpannableString(speed);
            s.setSpan(new RelativeSizeSpan(1f), s.length() - 4, s.length(), 0);
            getCurrentSpeed().setText(s);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onGpsStatusChanged(final int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                @NonNull final GpsStatus gpsStatus = getLocationManager().getGpsStatus(null);
                int satsInView = 0;
                int satsUsed = 0;
                @NonNull final Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
                for (@NonNull final GpsSatellite sat : sats) {
                    satsInView++;

                    if (sat.usedInFix()) {
                        satsUsed++;
                    }
                }

                if (satsUsed == 0) {
                    firstfix = true;
                }

                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                if (!getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGpsDisabledDialog();
                }

                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getData() != null && !getData().isRunning())
            super.onBackPressed();
    }

    @Override
    public void onStatusChanged(
            @NonNull final String s,
            final int i,
            @NonNull final Bundle bundle
    ) {
    }

    @Override
    public void onProviderEnabled(@NonNull final String s) {
    }

    @Override
    public void onProviderDisabled(@NonNull final String s) {
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        setMap(googleMap);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setMyLocationEnabled() {
        getMap().setMyLocationEnabled(true);
    }

    private void showGpsDisabledDialog() {
        @NonNull final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Настройки местоположения", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        builder.setTitle("GPS отключен")
                .setMessage("Для работы приложения откройте доступ к местоположению GPS")
                .setCancelable(false);
        builder.create().show();
    }

    private void drawRouteOnMap(@NonNull final GoogleMap map, @NonNull final List<LatLng> positions) {
        @NonNull final PolylineOptions options =
                new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);

        options.addAll(positions);

        @NonNull final Polyline polyline = map.addPolyline(options);
        @NonNull final LatLng target =
                new LatLng(
                        positions.get(positions.size() - 1).latitude,
                        positions.get(positions.size() - 1).longitude
                );

        @NonNull final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(15)
                .bearing(90)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void refreshMap(@NonNull final GoogleMap mapInstance) {
        mapInstance.clear();
    }

    private void initMap() {
        getMapFragment().getMapAsync(this);
        @Nullable final View mapView = getMapFragment().getView();

        if (mapView != null) {
            mapView.setVisibility(View.INVISIBLE);
        }
    }

    private void initClickListeners() {
        getChronometer().setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @SuppressLint("SetTextI18n")
            @Override
            public void onChronometerTick(@NonNull final Chronometer chrono) {
                long time;

                if (getData() == null) {
                    setData();
                }

                if (getData().isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    getData().setTime(time);
                } else {
                    time = getData().getTime();
                }

                final int h = (int) (time / 3600000);
                final int m = (int) (time - h * 3600000) / 60000;
                final int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                @NonNull final String hh = h < 10 ? "0" + h : h + "";
                @NonNull final String mm = m < 10 ? "0" + m : m + "";
                @NonNull final String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);
                currentTime = hh + ":" + mm + ":" + ss;

                if (getData().isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });

        getMapButton().setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public final void onClick(@NonNull final View v) {
                @Nullable final View mapView = getMapFragment().getView();

                if (mapView == null) {
                    return;
                }

                if (!mapActive) {
                    mapActive = true;
                    mapView.setVisibility(View.VISIBLE);
                    startLocation = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (startLocation != null) {
                        final double lat = startLocation.getLatitude();
                        final double lon = startLocation.getLongitude();
                        @NonNull final LatLng startPoint = new LatLng(lat, lon);
                        @NonNull final CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(startPoint)
                                .zoom(15)
                                .build();

                        getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } else {
                    mapActive = false;
                    mapView.setVisibility(View.INVISIBLE);
                }
            }
        });

        getStartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(@NonNull final View v) {
                if (getData() == null) {
                    setData();
                }

                if (!getData().isRunning()) {
                    getData().setRunning(true);
                    getCatView().run();
                    getStartButton().setBackground(getResources().getDrawable(R.drawable.pause));
                    getStopButton().setEnabled(false);
                    getStopButton().setBackground(getResources().getDrawable(R.drawable.stop_inactive));
                    getChronometer().setBase(SystemClock.elapsedRealtime() - getData().getTime());
                    getChronometer().start();
                    getData().setFirstTime(true);
                    startService(new Intent(getBaseContext(), MyService.class));
                } else {
                    getData().setRunning(false);
                    getCatView().stop();
                    getStartButton().setBackground(getResources().getDrawable(R.drawable.play));
                    getStopButton().setEnabled(true);
                    getStopButton().setBackground(getResources().getDrawable(R.drawable.stop));
                    stopService(new Intent(getBaseContext(), MyService.class));
                }
            }
        });

        getStopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(@NonNull final View v) {
                //Intent intent = new Intent(this, MainActivity.class);
                //intent.putExtra("distance", data.getDistance());
                //intent.putExtra("average", data.getAverageSpeed());
                //intent.putExtra("chronometer", currentTime);

//                if (getData() != null && user != null && currentTime != null) {
//                    @NonNull final Map<String, Object> training = new HashMap<>();
//
//                    training.put("distance", getData().getDistance());
//                    training.put("averageSpeed", getData().getAverageSpeed());
//                    training.put("chronometer", currentTime);
//                    training.put("startTime", Calendar.getInstance().getTime());
//                    db.collection("users")
//                            .document(user.getUid())
//                            .collection("trainings")
//                            .add(training);
//                    resetData();
//                }

                //startActivity(intent);
                finish();

            }
        });
    }

    private void dressUp() {
        @NonNull final SharedPreferences prefs = getSharedPreferences("auth_data", Context.MODE_PRIVATE);

        //noinspection ConstantConditions
        @NonNull final String preferedSkin = prefs.getString("ad_current_skin", CatView.Skins.COMMON.toString().toLowerCase());

        //noinspection ConstantConditions
        switch (preferedSkin) {
            case "bad":
                getCatView().setSkin(CatView.Skins.BAD);
                break;

            case "karate":
                getCatView().setSkin(CatView.Skins.KARATE);
                break;

            case "business":
                getCatView().setSkin(CatView.Skins.BUSINESS);
                break;

            case "normal":
                getCatView().setSkin(CatView.Skins.NORMAL);
                break;

            default:
                getCatView().setSkin(CatView.Skins.COMMON);
        }
    }

}