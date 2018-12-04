package com.myrungo.rungo.profile.stats

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.myrungo.rungo.profile.stats.models.UserTimeIntervalsInfo

@StateStrategyType(AddToEndSingleStrategy::class)
interface StatsView : MvpView {
    fun showMessage(message: String?)
    fun showInfo(trainings: UserTimeIntervalsInfo)
    fun showProgress(show: Boolean)
}