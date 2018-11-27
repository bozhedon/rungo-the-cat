package com.myrungo.rungo

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AppPresenter @Inject constructor(
    private val router: Router,
    private val authData: AuthHolder,
    private val catController: CatController
) : MvpPresenter<MvpView>() {

    fun initMainScreen(account: GoogleSignInAccount) {
        account.account?.let { authData.name = account.account!!.name }
        catController.setSkin(authData.currentSkin)
        router.newRootScreen(Screens.MainFlow)
    }

    fun initAuthScreen() {
        router.newRootScreen(Screens.AuthFlow)
    }
}