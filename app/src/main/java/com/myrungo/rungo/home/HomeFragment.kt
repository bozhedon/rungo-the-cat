package com.myrungo.rungo.home

import android.os.Bundle
import android.view.animation.Animation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.cat.CatView
import kotlinx.android.synthetic.main.fragment_home.*
import toothpick.Toothpick

class HomeFragment : BaseFragment(), HomeView {
    override val layoutRes = R.layout.fragment_home

    @InjectPresenter
    lateinit var presenter: HomePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(HomePresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cat.setOnClickListener { slap() }

        cat.setAnimationListener(object : Animation.AnimationListener {
            private var currentHead = cat.currentHead

            override fun onAnimationStart(animation: Animation) {
                cat.setHead(CatView.Heads.SHOCK)
            }

            override fun onAnimationEnd(animation: Animation) {
                cat.setHead(currentHead)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    override fun showCat(skin: CatView.Skins) {
        cat.setSkin(skin)
    }

    override fun greet() {
        cat.greet()
    }

    override fun slap() {
        cat.slap()
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}