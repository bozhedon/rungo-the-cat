package com.myrungo.rungo.main

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.main.choice.ChoiceFragment
import kotlinx.android.synthetic.main.fragment_main.*
import ru.terrakok.cicerone.android.support.SupportAppScreen
import toothpick.Toothpick

class MainFlowFragment : BaseFragment(), MainView {
    override val layoutRes = R.layout.fragment_main

    private val currentTabFragment: BaseFragment?
        get() = childFragmentManager.fragments.firstOrNull { !it.isHidden } as? BaseFragment

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter = Toothpick
        .openScope(Scopes.APP)
        .getInstance(MainPresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isFirstLaunch(savedInstanceState)) {
            Toothpick.inject(this, Toothpick.openScope(Scopes.APP))
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigation.setOnItemClickListener { presenter.onNavigationClicked(it) }
    }

    override fun showScreen(screen: SupportAppScreen) {
        val currentFragment = currentTabFragment
        val newFragment = childFragmentManager.findFragmentByTag(screen.screenKey)

        if (currentFragment != null && newFragment != null && currentFragment == newFragment) return

        childFragmentManager.beginTransaction().apply {
            if (newFragment == null) add(R.id.main_container, screen.fragment, screen.screenKey)

            currentFragment?.let {
                hide(it)
                it.userVisibleHint = false
            }
            newFragment?.let {
                show(it)
                it.userVisibleHint = true
            }
        }.commitNow()
    }

    override fun showChoice() {
        if (childFragmentManager.findFragmentByTag(CHOICE_TAG) == null) {
            ChoiceFragment().show(childFragmentManager, CHOICE_TAG)
            childFragmentManager.executePendingTransactions()
        }
    }

    override fun setCurrentPosition(currentPosition: Int) {
        navigation.currentPosition = currentPosition
    }

    override fun onBackPressed() {
        currentTabFragment?.onBackPressed()
    }

    companion object {
        private const val CHOICE_TAG = "mff_choice"
    }
}