package com.myrungo.rungo.run

import android.support.design.widget.Snackbar
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.model.database.entity.LocationDb

@StateStrategyType(AddToEndSingleStrategy::class)
interface RunView : MvpView {
    fun showSkin(skin: CatView.Skins)
    fun showMap(show: Boolean)
    fun showTime(curTime: String, challengeTime: String)
    fun showSpeed(curSpeed: Double, avgSpeed: Double)
    fun showDistance(curDistance: String, challengeDistance: String)
    fun run(isRun: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun drawRoute(locationDb: LocationDb)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(title: String, msg: String, tag: String)

    fun showMessage(message: String?, duration: Int = Snackbar.LENGTH_LONG)
}