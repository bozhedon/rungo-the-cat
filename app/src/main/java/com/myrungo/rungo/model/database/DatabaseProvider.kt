package com.myrungo.rungo.model.database

import android.arch.persistence.room.Room
import android.content.Context
import javax.inject.Inject
import javax.inject.Provider

class DatabaseProvider @Inject constructor(
    private val context: Context
) : Provider<AppDatabase> {

    override fun get() = Room
        .databaseBuilder(context, AppDatabase::class.java, "DB_APP")
        .build()
}
