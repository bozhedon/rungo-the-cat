package com.myrungo.rungo.shit.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.myrungo.rungo.shit.base.BaseContract;
import com.yandex.metrica.YandexMetrica;

public abstract class BaseFragment<V extends BaseContract.View, P extends BaseContract.Presenter<V>>
        extends Fragment
        implements BaseContract.View {

    protected abstract P getPresenter();

    protected abstract void setupPresenter();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPresenter();
        //noinspection unchecked
        getPresenter().onBindView((V) this);
    }

    @Override
    public void onDestroy() {
        getPresenter().onUnbindView();
        super.onDestroy();
    }

    @Override
    public void showMessage(@Nullable final String message) {
        if (message != null) {
            @Nullable final FragmentActivity activity = getActivity();

            if (activity != null) {
                Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    protected void reportError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

}
