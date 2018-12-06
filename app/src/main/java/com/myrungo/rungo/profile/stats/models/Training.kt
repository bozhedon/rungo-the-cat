package com.myrungo.rungo.profile.stats.models

import com.google.firebase.firestore.PropertyName

data class Training(

    @get:PropertyName("averageSpeed")
    @PropertyName("averageSpeed")
    val averageSpeedInKmH: Double = 0.0,

    @get:PropertyName("distance")
    @PropertyName("distance")
    val distanceInKm: Double = 0.0,

    @get:PropertyName("startTime")
    @PropertyName("startTime")
    val startTime: Long = 0,

    @get:PropertyName("endTime")
    @PropertyName("endTime")
    val endTime: Long = 0

)