package com.myrungo.rungo.model.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "location")
data class LocationDb(
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)