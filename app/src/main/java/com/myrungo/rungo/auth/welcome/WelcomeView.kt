package com.myrungo.rungo.auth.welcome

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface WelcomeView : MvpView {
    fun showProgress(show: Boolean)
}