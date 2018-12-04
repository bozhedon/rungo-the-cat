package com.myrungo.rungo.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.myrungo.rungo.model.database.dao.LocationDao
import com.myrungo.rungo.model.database.entity.LocationDb

@Database(
    entities = [LocationDb::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val locationDao: LocationDao
}
