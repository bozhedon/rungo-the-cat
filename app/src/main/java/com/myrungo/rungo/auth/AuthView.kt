package com.myrungo.rungo.auth

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.firebase.ui.auth.FirebaseUiException

@StateStrategyType(AddToEndSingleStrategy::class)
interface AuthView : MvpView {
    fun showButton(show: Boolean)
    fun showProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun signIn()

    fun handleSignInError(error: FirebaseUiException)
    fun showMessage(message: String?)
}