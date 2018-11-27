package com.myrungo.rungo.main.choice

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.model.MainNavigationController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class ChoicePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController
) : BasePresenter<ChoiceView>() {

    fun onChallengeClicked() {
        viewState.dismiss()
        navigation.open(3)
    }

    fun onTrainingClicked() {
        viewState.dismiss()
        router.navigateTo(Screens.Start)
    }

    fun onBackPressed() = viewState.dismiss()
}