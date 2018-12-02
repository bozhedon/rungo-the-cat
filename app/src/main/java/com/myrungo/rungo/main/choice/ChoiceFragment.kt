package com.myrungo.rungo.main.choice

import android.Manifest
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_choice.*
import timber.log.Timber
import toothpick.Toothpick

class ChoiceFragment : DialogFragment(), ChoiceView {
    private val mvpDelegate: MvpDelegate<out ChoiceFragment> by lazy { MvpDelegate(this) }
    private var disposable: Disposable? = null

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
        choice_training_button.setOnClickListener { checkPermission() }
        choice_training_text.setOnClickListener { checkPermission() }
    }

    private fun checkPermission() {
        disposable = RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .subscribe(
                { isGranted ->
                    if (isGranted) {
                        presenter.onTrainingClicked()
                    }
                },
                { Timber.e(it) }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }
}