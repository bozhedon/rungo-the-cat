package com.myrungo.rungo.model

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class MainNavigationController {
    private val stateRelay = PublishRelay.create<Int>()

    val screenState: Observable<Int> = stateRelay
    fun open(position: Int) = stateRelay.accept(position)
}