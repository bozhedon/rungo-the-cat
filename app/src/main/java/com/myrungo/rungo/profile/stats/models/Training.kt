package com.myrungo.rungo.profile.stats.models

data class Training(
    /**в км/ч*/
    val averageSpeed: Double = 0.0,

    /**в километрах*/
    val distance: Double = 0.0,

    val startTime: Long = 0,

    val endTime: Long = 0
)