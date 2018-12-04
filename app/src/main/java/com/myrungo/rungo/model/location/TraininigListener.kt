package com.myrungo.rungo.model.location

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class TraininigListener {
    private val infoRelay = PublishRelay.create<Double>()
    var isRun = false

    fun listen(): Observable<Double> = infoRelay
    fun send(distance: Double) = infoRelay.accept(distance)
}