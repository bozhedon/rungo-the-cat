package com.myrungo.rungo.challenge

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ChallengeView : MvpView {
    fun showData(data: List<Any>)
    fun showProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAcceptDialog(distance: String, time: String, resId: Int)
}