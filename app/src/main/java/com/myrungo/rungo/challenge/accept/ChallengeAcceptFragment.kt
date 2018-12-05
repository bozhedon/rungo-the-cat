package com.myrungo.rungo.challenge.accept

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
import com.bumptech.glide.Glide
import com.myrungo.rungo.BuildConfig
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.utils.showAlertDialog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_challenge_accept.*
import toothpick.Toothpick

class ChallengeAcceptFragment : DialogFragment(), ChallengeAcceptView {
    private val mvpDelegate: MvpDelegate<out ChallengeAcceptFragment> by lazy { MvpDelegate(this) }
    private val challenge get() = arguments?.getParcelable<ChallengeItem>(ARG_CHALLENGE)
    private var disposable: Disposable? = null

    @InjectPresenter
    lateinit var presenter: ChallengeAcceptPresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(ChallengeAcceptPresenter::class.java)!!

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
    ) = LayoutInflater.from(context).inflate(R.layout.fragment_challenge_accept, null)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.setOnClickListener { dismiss() }
        challenge_accept_card.setOnClickListener { }
        challenge_cancel_button.setOnClickListener { dismiss() }
        challenge_accept_button.setOnClickListener { presenter.onChallengeAcceptClicked(this) }
        challenge_distance_text.text =
                getString(R.string.distance, challenge?.distance?.toFloat() ?: 0f)
        challenge_time_text.text = challenge?.let { "${it.time / 100}:${it.time % 100}" } ?: ""
        Glide.with(this)
            .load(challenge?.awardRes)
            .into(challenge_accept_award_image)
    }

    override fun onPermissionGranted() {
        presenter.accept(challenge)
        dismissAllowingStateLoss()
        childFragmentManager.executePendingTransactions()
    }

    override fun showNeedLocationPermissionRationaleDialog() {
        val part1 = getString(R.string.application_needs_permissions_to_location)
        val part2 = getString(R.string.provide)

        val message = "$part1. $part2?"

        context?.showAlertDialog(
            message,
            getString(R.string.yes),
            getString(R.string.no),
            { presenter.onChallengeAcceptClicked(this) },
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

    private fun onApplicationSettingsRequestForGetCurrentLocationCode(resultCode: Int) {
        if (resultCode == 0) {
            presenter.onChallengeAcceptClicked(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    companion object {
        const val APPLICATION_SETTINGS_REQUEST = 1000

        private const val ARG_CHALLENGE = "caf_challenge"
        fun newInstance(challenge: ChallengeItem) = ChallengeAcceptFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CHALLENGE, challenge)
                }
            }
    }
}