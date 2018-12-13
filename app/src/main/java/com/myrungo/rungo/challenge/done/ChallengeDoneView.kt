package com.myrungo.rungo.challenge.done

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface ChallengeDoneView : MvpView{
    fun showLayout(resultState: Int)
    fun showDistance(curDistanceResult: String, challengeDistance: String)
    fun showTime(curTimeResult: String)
    fun showSpeed(curSpeedResult: String, avgSpeedResult: String)
    fun showGift()
}