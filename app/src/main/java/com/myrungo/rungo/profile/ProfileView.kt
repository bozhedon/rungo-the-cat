package com.myrungo.rungo.profile

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProfileView : MvpView {
    fun showCat(resId: Int)
    fun showDetails(name: String, distance: Float)
    fun showTab(position: Int)
}