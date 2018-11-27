package com.myrungo.rungo

import android.content.Context
import android.content.Intent
import com.myrungo.rungo.auth.AuthFlowFragment
import com.myrungo.rungo.auth.AuthFragment
import com.myrungo.rungo.auth.welcome.WelcomeFragment
import com.myrungo.rungo.challenge.ChallengeFragment
import com.myrungo.rungo.customize.CustomizeFragment
import com.myrungo.rungo.customize_done.CustomizeDoneFragment
import com.myrungo.rungo.home.HomeFragment
import com.myrungo.rungo.main.MainFlowFragment
import com.myrungo.rungo.profile.ProfileFragment
import com.myrungo.rungo.run.RunFlowFragment
import com.myrungo.rungo.run.RunFragment
import com.myrungo.rungo.shit.start.StartActivity
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object MainFlow : SupportAppScreen() {
        override fun getFragment() = MainFlowFragment()
    }

    object AuthFlow : SupportAppScreen() {
        override fun getFragment() = AuthFlowFragment()
    }

    object Auth : SupportAppScreen() {
        override fun getFragment() = AuthFragment()
    }

    object Welcome : SupportAppScreen() {
        override fun getFragment() = WelcomeFragment()
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

    object RunFlow : SupportAppScreen() {
        override fun getFragment() = RunFlowFragment()
    }

    object Run : SupportAppScreen() {
        override fun getFragment() = RunFragment()
    }

    object Start : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, StartActivity::class.java)
    }
}