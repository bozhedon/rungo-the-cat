package com.myrungo.rungo.profile.stats.models

import com.google.firebase.firestore.PropertyName

//annotations needed for deserialize
data class Challenge(

    @get:PropertyName("distance")
    @PropertyName("distance")
    val distanceInKm: Double = 0.0,

    @get:PropertyName("hour")
    @PropertyName("hour")
    val hour: Double = 0.0,

    @get:PropertyName("id")
    @PropertyName("id")
    val id: Int = 0,

    @get:PropertyName("imgURL")
    @PropertyName("imgURL")
    val imgURL: String = "",

    @get:PropertyName("isComplete")
    @PropertyName("isComplete")
    val isComplete: Boolean = false,

    @get:PropertyName("minutes")
    @PropertyName("minutes")
    val minutes: Double = 0.0,

    @get:PropertyName("reward")
    @PropertyName("reward")
    val reward: String = "",

    @get:PropertyName("startTime")
    @PropertyName("startTime")
    val startTime: Long = 0,

    @get:PropertyName("endTime")
    @PropertyName("endTime")
    val endTime: Long = 0,

    @get:PropertyName("averageSpeed")
    @PropertyName("averageSpeed")
    val averageSpeedInKmH: Double = 0.0

)