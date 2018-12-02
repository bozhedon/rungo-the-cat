package com.myrungo.rungo.challenge.accept

import android.Manifest
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.challenge.ChallengeItem
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_challenge_accept.*
import timber.log.Timber
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
        .getInstance(ChallengeAcceptPresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
        setStyle(STYLE_NORMAL, R.style.OverlayTheme)
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        LayoutInflater.from(context).inflate(R.layout.fragment_challenge_accept, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.setOnClickListener { dismiss() }
        challenge_accept_card.setOnClickListener { }
        challenge_cancel_button.setOnClickListener { dismiss() }
        challenge_accept_button.setOnClickListener {
            disposable = RxPermissions(this)
                .request(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(
                    { isGranted ->
                        if (isGranted) {
                            presenter.accept(challenge)
                            dismissAllowingStateLoss()
                            childFragmentManager.executePendingTransactions()
                        }
                    },
                    { Timber.e(it) }
                )
        }
        challenge_distance_text.text = getString(R.string.distance, challenge?.distance?.toFloat() ?: 0f)
        challenge_time_text.text = challenge?.let { "${it.time/100}:${it.time%100}" } ?: ""
        Glide.with(this)
            .load(challenge?.awardRes)
            .into(challenge_accept_award_image)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    companion object {
        private const val ARG_CHALLENGE = "caf_challenge"
        fun newInstance(challenge: ChallengeItem) = ChallengeAcceptFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CHALLENGE, challenge)
                }
            }
    }
}