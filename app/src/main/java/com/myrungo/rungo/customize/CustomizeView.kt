package com.myrungo.rungo.customize

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface CustomizeView : MvpView {
    fun showSkinReference(resId: Int)
    fun showSkins(skins: List<SkinItem>)
    fun showProgress(show: Boolean)
}