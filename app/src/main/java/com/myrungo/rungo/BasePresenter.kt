package com.myrungo.rungo

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Task
import com.yandex.metrica.YandexMetrica
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    protected fun Disposable.connect() {
        compositeDisposable.add(this)
    }

    protected fun report(throwable: Throwable) {
        Timber.e(throwable)
        Crashlytics.logException(throwable)
        YandexMetrica.reportUnhandledException(throwable)
    }

    protected fun waitForAnyResult(task: Task<*>) {
        //for sync realization
        while (true) {
            val complete = task.isComplete

            val canceled = task.isCanceled

            val successful = task.isSuccessful

            val msg = "task.isComplete() == " +
                    complete +
                    "; task.isCanceled() == " +
                    canceled +
                    "; task.isSuccessful() == " +
                    successful

            Timber.d(msg)

            if (successful || canceled || complete) {
                break
            }
        }
    }
}