package com.myrungo.rungo.challenge.accept

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class ChallengeAcceptPresenter @Inject constructor(
    private val router: Router
) : BasePresenter<ChallengeAcceptView>() {

    fun accept() {
        router.navigateTo(Screens.RunFlow(false))
    }
}