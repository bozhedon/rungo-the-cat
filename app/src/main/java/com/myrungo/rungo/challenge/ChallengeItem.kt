package com.myrungo.rungo.challenge

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChallengeItem(
    val id: Int,
    val distance: Int,
    val time: Int,
    val awardRes: Int,
    var isComplete: Boolean = false
) : Parcelable