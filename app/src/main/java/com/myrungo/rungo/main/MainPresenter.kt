package com.myrungo.rungo.main

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.model.MainNavigationController
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController
): BasePresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        onNavigationClicked(0)

        navigation.screenState
            .subscribe(
                { onNavigationClicked(it) },
                { Timber.e(it) }
            )
            .connect()
    }

    fun onNavigationClicked(position: Int) {
        if (position == 2) {
            viewState.showChoice()
            return
        }

        viewState.showScreen(
            when (position) {
                0 -> homeScreen
                1 -> customScreen
                3 -> challengeScreen
                4 -> profileScreen
                else -> return
            }
        )
        viewState.setCurrentPosition(position)
    }

    companion object {
        private val homeScreen = Screens.Home
        private val customScreen = Screens.Customize
        private val challengeScreen = Screens.Challenge
        private val profileScreen = Screens.Profile
    }
}