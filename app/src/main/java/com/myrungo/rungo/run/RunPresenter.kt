package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import javax.inject.Inject

@InjectViewState
class RunPresenter @Inject constructor(

) : BasePresenter<RunView>() {

    fun onBackPressed() {

    }
}