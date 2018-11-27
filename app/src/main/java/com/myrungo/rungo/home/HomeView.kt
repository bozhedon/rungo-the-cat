package com.myrungo.rungo.home

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.myrungo.rungo.cat.CatView

@StateStrategyType(AddToEndSingleStrategy::class)
interface HomeView : MvpView {
    fun showCat(skin: CatView.Skins)
    fun greet()
    fun slap()
}