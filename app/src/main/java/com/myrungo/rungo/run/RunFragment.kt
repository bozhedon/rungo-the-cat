package com.myrungo.rungo.run

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.cat.CatView
import kotlinx.android.synthetic.main.fragment_run.*
import toothpick.Toothpick

class RunFragment : BaseFragment(), RunView {
    override val layoutRes = R.layout.fragment_run

    @InjectPresenter
    lateinit var presenter: RunPresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.PLAY)
        .getInstance(RunPresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun showSkin(skin: CatView.Skins) {
        cat_view.setSkin(skin)
    }

    override fun showDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run(isRun: Boolean) {
        if (isRun) {

        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}