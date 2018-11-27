package com.myrungo.rungo.run

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.myrungo.rungo.cat.CatView

@StateStrategyType(AddToEndSingleStrategy::class)
interface RunView : MvpView {
    fun showSkin(skin: CatView.Skins)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun run(isRun: Boolean)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog()
}