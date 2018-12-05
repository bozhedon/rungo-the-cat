package com.myrungo.rungo.challenge.accept

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface ChallengeAcceptView : MvpView {
    fun dismiss()
    fun onPermissionGranted()
    fun showNeedLocationPermissionRationaleDialog()
    fun showGoSettingsDialog()
    fun startSettingsActivityForResult()
}