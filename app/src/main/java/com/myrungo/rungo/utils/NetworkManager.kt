package com.myrungo.rungo.utils

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class NetworkManager @Inject constructor(private val context: Context) {

    private val connectivityManager
        get(): ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnectedToInternet: Boolean
        get() {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false

            return networkInfo.isConnected
        }

}