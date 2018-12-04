package com.myrungo.rungo.profile.stats.models

data class Challenge(
    val distance: Double = 0.0,

    val hour: Int = 0,

    val id: Int = 0,

    val imgURL: String = "",

    val isComplete: Boolean = false,

    val minutes: Int = 0,

    val reward: String = "",

    val startTime: Long = 0,

    val endTime: Long = 0,

    val averageSpeed: Double = 0.0
)