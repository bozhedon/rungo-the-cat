package com.myrungo.rungo.shit.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public final class MyTrackerReceiver extends BroadcastReceiver {

    @NonNull
    public static final String TAG = MyTrackerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        @Nullable final String installationSource = intent.getStringExtra("referrer");
        if (installationSource != null) {
            Log.i(TAG, String.format("Referrer received: %s", installationSource));
        }
    }

}
