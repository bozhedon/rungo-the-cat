package com.myrungo.rungo.customize_done

import android.os.Bundle
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import kotlinx.android.synthetic.main.fragment_customize_done.*
import toothpick.Toothpick

class CustomizeDoneFragment : BaseFragment(), MvpView {
    override val layoutRes = R.layout.fragment_customize_done
    private val skinRes get() = arguments?.getInt(ARG_SKIN, 0)

    @InjectPresenter
    lateinit var presenter: CustomizeDonePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(CustomizeDonePresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (skinRes != null && skinRes != 0) {
            Glide.with(this)
                .load(skinRes)
                .into(custom_done_cat_image)
        }

        custom_done_button.setOnClickListener { presenter.onBackPressed(true) }
    }

    override fun onBackPressed() {
        presenter.onBackPressed(false)
    }

    companion object {
        private const val ARG_SKIN = "cdf_skin"
        fun newInstance(skinRes: Int) = CustomizeDoneFragment()
            .apply {
                arguments = Bundle().apply { putInt(ARG_SKIN, skinRes) }
            }
    }
}