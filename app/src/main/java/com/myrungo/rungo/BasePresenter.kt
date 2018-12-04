package com.myrungo.rungo

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.crashlytics.android.Crashlytics
import com.yandex.metrica.YandexMetrica
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    protected fun Disposable.connect() {
        compositeDisposable.add(this)
    }

    protected fun reportError(throwable: Throwable) {
        Crashlytics.logException(throwable)
        YandexMetrica.reportUnhandledException(throwable)
    }
}