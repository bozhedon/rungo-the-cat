package com.myrungo.rungo.shit.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface BaseContract {

    interface View {

        void showMessage(@Nullable final String message);

    }

    interface Presenter<V extends View> {

        void onBindView(@NonNull final V view);

        void onUnbindView();

        void onViewCreate();

    }

}
