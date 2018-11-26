package com.myrungo.rungo.profile

import android.os.Bundle
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_profile

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profile_stats_navigation.setOnItemClickListener {  }
    }
}