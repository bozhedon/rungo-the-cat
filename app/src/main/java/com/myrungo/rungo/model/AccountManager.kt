package com.myrungo.rungo.model

import android.accounts.AccountManager
import android.content.Context
import javax.inject.Inject

class AccountManager @Inject constructor(context: Context) {
    private val manager = context.getSystemService(Context.ACCOUNT_SERVICE) as? AccountManager
    val account = manager?.accounts
}