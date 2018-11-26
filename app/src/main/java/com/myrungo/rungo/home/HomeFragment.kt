package com.myrungo.rungo.home

import android.os.Bundle
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import toothpick.Toothpick

class HomeFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isFirstLaunch(savedInstanceState)) {
            Toothpick.inject(this, Toothpick.openScope(Scopes.APP))
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}