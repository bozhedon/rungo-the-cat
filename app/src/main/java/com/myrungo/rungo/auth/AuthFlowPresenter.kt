package com.myrungo.rungo.auth

import com.arellomobile.mvp.MvpView
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Scopes
import ru.terrakok.cicerone.Router
import toothpick.Toothpick
import javax.inject.Inject

class AuthFlowPresenter @Inject constructor(
    private val router: Router
) : BasePresenter<MvpView>() {

    override fun onDestroy() {
        Toothpick.closeScope(Scopes.AUTH)
        super.onDestroy()
    }

    fun onExit() {
        router.exit()
    }
}