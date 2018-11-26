package com.myrungo.rungo

import com.myrungo.rungo.challenge.ChallengeFragment
import com.myrungo.rungo.customize.CustomizeFragment
import com.myrungo.rungo.customize_done.CustomizeDoneFragment
import com.myrungo.rungo.home.HomeFragment
import com.myrungo.rungo.main.MainFlowFragment
import com.myrungo.rungo.profile.ProfileFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object MainFlow : SupportAppScreen() {
        override fun getFragment() = MainFlowFragment()
    }

    object Home : SupportAppScreen() {
        override fun getFragment() = HomeFragment()
    }

    object Customize : SupportAppScreen() {
        override fun getFragment() = CustomizeFragment()
    }

    object Challenge : SupportAppScreen() {
        override fun getFragment() = ChallengeFragment()
    }

    object Profile : SupportAppScreen() {
        override fun getFragment() = ProfileFragment()
    }

    data class CustomizeDone(val skinRes: Int) : SupportAppScreen() {
        override fun getFragment() = CustomizeDoneFragment.newInstance(skinRes)
    }
}