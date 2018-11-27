package com.myrungo.rungo.profile

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.profile.stats.StatsFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import toothpick.Toothpick

class ProfileFragment : BaseFragment(), ProfileView {
    override val layoutRes = R.layout.fragment_profile

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(ProfilePresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profile_stats_navigation.setOnItemClickListener { presenter.onTabClicked(it) }
    }

    override fun showCat(resId: Int) {
        Glide.with(this)
            .load(resId)
            .into(profile_cat_image)
    }

    override fun showDetails(name: String, distance: String) {
        profile_name.text = name
        profile_total_distance.text = distance
    }

    override fun showTab(position: Int) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.profile_stats_container, StatsFragment.newInstance(position))
            .commitNow()

        profile_stats_navigation.currentTab = position
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}