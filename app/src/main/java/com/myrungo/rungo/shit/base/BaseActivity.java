package com.myrungo.rungo.shit.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.yandex.metrica.YandexMetrica;

abstract public class BaseActivity<V extends BaseContract.View, P extends BaseContract.Presenter<V>>
        extends AppCompatActivity
        implements BaseContract.View {

    protected abstract P getPresenter();

    protected abstract void setupPresenter();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPresenter();
        //noinspection unchecked
        getPresenter().onBindView((V) this);
    }

    @Override
    protected void onDestroy() {
        getPresenter().onUnbindView();
        super.onDestroy();
    }

    @Override
    public void showMessage(@Nullable final String message) {
        if (message != null) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    protected void reportError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

    @Nullable
    private SharedPreferences sharedPreferences;

    @NonNull
    protected SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        return sharedPreferences;
    }

}
