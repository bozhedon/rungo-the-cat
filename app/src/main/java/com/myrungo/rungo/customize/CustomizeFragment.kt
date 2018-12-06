package com.myrungo.rungo.customize

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.fragment_customize.*
import toothpick.Toothpick

class CustomizeFragment : BaseFragment(), CustomizeView {
    override val layoutRes = R.layout.fragment_customize
    private val adapter by lazy { SkinAdapter() }

    @InjectPresenter
    lateinit var presenter: CustomizePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(CustomizePresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        custom_recycler_view.adapter = adapter
        custom_check_button.setOnClickListener { presenter.onSelectClicked() }
        custom_default_button.setOnClickListener { presenter.onDefaultSelected() }
    }

    override fun onStart() {
        super.onStart()

        presenter.onStart()
    }

    override fun showSkinReference(resId: Int) {
        Glide.with(this)
            .load(resId)
            .into(custom_cat_image)
    }

    override fun showSkins(skins: List<SkinItem>) {
        postViewAction { adapter.setData(skins) }
    }

    override fun showProgress(show: Boolean) {
        custom_layout.visible(!show)
        custom_progress.visible(show)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private inner class SkinAdapter : ListDelegationAdapter<MutableList<Any>>() {
        init {
            items = mutableListOf()
            delegatesManager.addDelegate(SkinAdapterDelegate { presenter.onSkinClicked(it) })
        }

        fun setData(data: List<Any>) {
            items.clear()
            items.addAll(data)
            //todo use DiffUtils
            notifyDataSetChanged()
        }
    }
}