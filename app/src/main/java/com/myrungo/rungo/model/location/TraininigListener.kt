package com.myrungo.rungo.model.location

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class TraininigListener {
    private val infoRelay = PublishRelay.create<TrainingInfo>()
    var isRun = false

    fun listen(): Observable<TrainingInfo> = infoRelay
    fun send(distance: Double, speed: Double) = infoRelay.accept(TrainingInfo(distance, speed))
}