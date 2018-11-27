package com.myrungo.rungo.shit.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragmentPresenter<V extends BaseFragmentContract.View>
        extends BasePresenter<V>
        implements BaseFragmentContract.Presenter<V> {

    @NonNull
    protected FragmentActivity getActivity() {
        @NonNull final Fragment fragment = (Fragment) getView();

        @Nullable final FragmentActivity activity = fragment.getActivity();

        if (activity == null) {
            @NonNull final NullPointerException exception = new NullPointerException("activity == null");
            reportError(exception);

            throw exception;
        }

        return activity;
    }

}
