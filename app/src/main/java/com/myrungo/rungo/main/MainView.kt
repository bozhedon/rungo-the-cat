package com.myrungo.rungo.main

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.terrakok.cicerone.android.support.SupportAppScreen

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun showScreen(screen: SupportAppScreen)
    fun setCurrentPosition(currentPosition: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showChoice()
}