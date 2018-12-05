package com.myrungo.rungo.main.choice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BuildConfig
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.challenge.accept.ChallengeAcceptFragment.Companion.APPLICATION_SETTINGS_REQUEST
import com.myrungo.rungo.utils.showAlertDialog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_choice.choice_challenge_button
import kotlinx.android.synthetic.main.fragment_choice.choice_challenge_text
import kotlinx.android.synthetic.main.fragment_choice.choice_training_button
import kotlinx.android.synthetic.main.fragment_choice.choice_training_text
import toothpick.Toothpick

class ChoiceFragment : DialogFragment(), ChoiceView {
    private val mvpDelegate: MvpDelegate<out ChoiceFragment> by lazy { MvpDelegate(this) }
    private var disposable: Disposable? = null

    @InjectPresenter
    lateinit var presenter: ChoicePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(ChoicePresenter::class.java)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
        setStyle(STYLE_NORMAL, R.style.OverlayTheme)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutInflater.from(context).inflate(R.layout.fragment_choice, null)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.setOnClickListener { presenter.onBackPressed() }
        choice_challenge_button.setOnClickListener { presenter.onChallengeClicked() }
        choice_challenge_text.setOnClickListener { presenter.onChallengeClicked() }
        choice_training_button.setOnClickListener { presenter.onTrainingClicked(this) }
        choice_training_text.setOnClickListener { presenter.onTrainingClicked(this) }
    }

    override fun showNeedLocationPermissionRationaleDialog() {
        val part1 = getString(R.string.application_needs_permissions_to_location)
        val part2 = getString(R.string.provide)

        val message = "$part1. $part2?"

        context?.showAlertDialog(
            message,
            getString(R.string.yes),
            getString(R.string.no),
            { presenter.onTrainingClicked(this) },
            { },
            getString(R.string.impossible_to_continue)
        )
    }

    override fun showGoSettingsDialog() {
        val part1 =
            getString(R.string.go_to_applications_settings_and_provide_permissions_to_location)
        val part2 = getString(R.string.go_to)

        val message = "$part1. $part2?"

        context?.showAlertDialog(
            message,
            getString(R.string.yes),
            getString(R.string.no),
            { presenter.onGoSettingsForGetCurrentLocationPositiveClick() },
            { },
            getString(R.string.impossible_to_continue)
        )
    }

    override fun startSettingsActivityForResult() {
        // Build intent that displays the App settings screen.
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        }

        startActivityForResult(intent, APPLICATION_SETTINGS_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            APPLICATION_SETTINGS_REQUEST ->
                onApplicationSettingsRequestForGetCurrentLocationCode(resultCode)
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    private fun onApplicationSettingsRequestForGetCurrentLocationCode(resultCode: Int) {
        if (resultCode == 0) {
            presenter.onTrainingClicked(this)
        }
    }

}