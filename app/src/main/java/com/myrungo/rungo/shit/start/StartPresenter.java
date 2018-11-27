package com.myrungo.rungo.shit.start;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.myrungo.rungo.BuildConfig;
import com.myrungo.rungo.shit.base.BasePresenter;

import static com.myrungo.rungo.shit.start.StartActivity.REQUEST_APPLICATION_SETTINGS_FOR_ON_RESUME;

final class StartPresenter
        extends BasePresenter<StartContract.View>
        implements StartContract.Presenter<StartContract.View> {

    @Override
    public void onViewCreate() {
    }

    @Override
    public void onStart() {
        if (isLocationPermissionDeniedForOnStart()) return;

        getView().requestLocationUpdates();

        getView().setMyLocationEnabled();
    }

    @Override
    public void onRequestPermissionForOnStartResult(@NonNull final int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (isPermissionGranted(grantResults)) {
            // permission was granted
            onStart();
        } else if (shouldShowRequestLocationPermissionRationale()) {
            getView().showRequestPermissionRationaleForOnStart();
        } else {
            //user denied permission with tap on "dont ask"
            getView().showGoSettingsForOnStartDialog();
        }
    }

    @Override
    public void onShowGoSettingsForOnStartDialogPositiveButtonClick() {
        @NonNull final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));

        getActivity().startActivityForResult(intent, REQUEST_APPLICATION_SETTINGS_FOR_ON_RESUME);
    }

    @Override
    public void onShowGoSettingsForStartDialogNegativeButtonClick() {
        getActivity().finish();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnStartPositiveButtonClick() {
        getView().requestLocationPermissionForOnStart();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnStartNegativeButtonClick() {
        getActivity().finish();
    }

    private boolean isPermissionGranted(@NonNull final int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionDeniedForOnStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationPermissionDenied()) {
                // Permission is not granted
                // Should we show an explanation?
                if (shouldShowRequestLocationPermissionRationale()) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    getView().showRequestPermissionRationaleForOnStart();
                } else {
                    // No explanation needed; request the permission
                    getView().requestLocationPermissionForOnStart();
                }

                return true;
            }
        }

        return false;
    }

    private boolean shouldShowRequestLocationPermissionRationale() {
        return ActivityCompat
                .shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) && ActivityCompat
                .shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                );
    }

    private boolean isLocationPermissionDenied() {
        return ContextCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED;
    }

}
