package com.myrungo.rungo.challenge

import android.os.Parcel
import android.os.Parcelable

data class ChallengeItem(
    val id: Int,
    val distance: Int,
    val time: Int,
    val awardRes: Int,
    var isComplete: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(distance)
        parcel.writeInt(time)
        parcel.writeInt(awardRes)
        parcel.writeInt(isComplete)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChallengeItem> {
        override fun createFromParcel(parcel: Parcel): ChallengeItem {
            return ChallengeItem(parcel)
        }

        override fun newArray(size: Int): Array<ChallengeItem?> {
            return arrayOfNulls(size)
        }
    }

}