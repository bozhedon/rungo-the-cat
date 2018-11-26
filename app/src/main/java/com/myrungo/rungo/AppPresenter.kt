package com.myrungo.rungo

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AppPresenter @Inject constructor(
    private val router: Router
) : MvpPresenter<MvpView>() {

    fun coldStart() {
        router.newRootScreen(Screens.MainFlow)
    }
}