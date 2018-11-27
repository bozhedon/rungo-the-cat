package com.myrungo.rungo.shit.start;

import android.support.annotation.NonNull;
import com.myrungo.rungo.shit.base.BaseContract;

interface StartContract extends BaseContract {

    interface View extends BaseContract.View {

        void requestLocationUpdates();

        void setMyLocationEnabled();

        void showRequestPermissionRationaleForOnStart();

        void requestLocationPermissionForOnStart();

        void showGoSettingsForOnStartDialog();

    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        void onStart();

        void onRequestPermissionForOnStartResult(@NonNull final int[] grantResults);

        void onShowRequestPermissionRationaleForOnStartPositiveButtonClick();

        void onShowRequestPermissionRationaleForOnStartNegativeButtonClick();

        void onShowGoSettingsForOnStartDialogPositiveButtonClick();

        void onShowGoSettingsForStartDialogNegativeButtonClick();

    }

}
