package com.myrungo.rungo.model

import android.content.Context
import javax.inject.Inject

class ResourceManager @Inject constructor(private val context: Context) {

    fun getString(id: Int) = context.getString(id)

    fun getString(id: Int, vararg args: Any) = context.getString(id, args)
}