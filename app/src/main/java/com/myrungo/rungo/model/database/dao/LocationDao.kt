package com.myrungo.rungo.model.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.myrungo.rungo.model.database.entity.LocationDb
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: LocationDb)

    @Query("SELECT * FROM location")
    fun getLocationList(): Single<List<LocationDb>>

    @Query("SELECT * FROM location")
    fun listenLocation(): Flowable<List<LocationDb>>

    @Query("SELECT * FROM location ORDER BY id DESC LIMIT 1")
    fun getLastLocation(): Single<LocationDb>

    @Query("SELECT * FROM location ORDER BY id DESC LIMIT 1")
    fun listenLastLocation(): Flowable<LocationDb>

    @Query("DELETE FROM location WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM location")
    fun clear()
}