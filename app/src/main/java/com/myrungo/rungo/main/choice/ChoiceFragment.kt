package com.myrungo.rungo.main.choice

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import kotlinx.android.synthetic.main.fragment_choice.*
import toothpick.Toothpick

class ChoiceFragment : DialogFragment(), ChoiceView {
    private val mvpDelegate: MvpDelegate<out ChoiceFragment> by lazy { MvpDelegate(this) }

    @InjectPresenter
    lateinit var presenter: ChoicePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(ChoicePresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
        setStyle(STYLE_NORMAL, R.style.OverlayTheme)
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        LayoutInflater.from(context).inflate(R.layout.fragment_choice, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.setOnClickListener { presenter.onBackPressed() }
        choice_challenge_button.setOnClickListener { presenter.onChallengeClicked() }
        choice_challenge_text.setOnClickListener { presenter.onChallengeClicked() }
        choice_training_button.setOnClickListener { presenter.onTrainingClicked() }
        choice_training_text.setOnClickListener { presenter.onTrainingClicked() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }
}