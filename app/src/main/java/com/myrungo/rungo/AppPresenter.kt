package com.myrungo.rungo

import com.arellomobile.mvp.MvpView
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.model.database.AppDatabase
import io.reactivex.Completable
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class AppPresenter @Inject constructor(
    private val router: Router,
    private val authData: AuthHolder,
    private val catController: CatController,
    private val database: AppDatabase,
    private val schedulersProvider: SchedulersProvider
) : BasePresenter<MvpView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        Completable.fromCallable { database.locationDao.clear() }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe({}, { Timber.e(it) })
            .connect()
    }

    fun initMainScreen(account: GoogleSignInAccount) {
        authData.name = account.displayName ?: "User"

        catController.setSkin(authData.currentSkin)
        router.newRootScreen(Screens.MainFlow)
    }

    fun initAuthScreen() {
        router.newRootScreen(Screens.AuthFlow)
    }
    fun initWelcomeScreen() {
        router.newRootScreen(Screens.Welcome)
    }
}