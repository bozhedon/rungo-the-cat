package com.myrungo.rungo.home

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

    override fun showCat(skin: CatView.Skins) {
        cat.setSkin(skin)
    }

    override fun greet() {
        cat.greet()
    }

    override fun slap() {
        cat.slap()
    }

//    override fun onPause() {
//        cat.pause()
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        cat.resume()
//    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}