package com.myrungo.rungo.challenge.accept

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
import kotlinx.android.synthetic.main.fragment_challenge_accept.*
import toothpick.Toothpick

class ChallengeAcceptFragment : DialogFragment(), ChallengeAcceptView {
    private val mvpDelegate: MvpDelegate<out ChallengeAcceptFragment> by lazy { MvpDelegate(this) }

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
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        LayoutInflater.from(context).inflate(R.layout.fragment_challenge_accept, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        challenge_accept_button.setOnClickListener { presenter.accept() }
        challenge_distance_text.text = arguments?.getString(ARG_DISTANCE)
        challenge_time_text.text = arguments?.getString(ARG_TIME)
        Glide.with(this)
            .load(arguments?.getInt(ARG_AWARD))
            .into(challenge_accept_award_image)
    }

    override fun onDestroy() {
        super.onDestroy()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    companion object {
        private const val ARG_DISTANCE = "caf_distance"
        private const val ARG_TIME = "caf_time"
        private const val ARG_AWARD = "caf_award"
        fun newInstance(distance: String, time: String, resId: Int) = ChallengeAcceptFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(ARG_DISTANCE, distance)
                    putString(ARG_TIME, time)
                    putInt(ARG_AWARD, resId)
                }
            }
    }
}