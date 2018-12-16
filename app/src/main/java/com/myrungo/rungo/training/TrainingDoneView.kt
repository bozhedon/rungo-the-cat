package com.myrungo.rungo.training

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface TrainingDoneView : MvpView{
    fun showDistance(trainingDistanceResult: String)
    fun showTime(curTimeResult: String)
    fun showSpeed(curSpeedResult: String, avgSpeedResult: String)
}