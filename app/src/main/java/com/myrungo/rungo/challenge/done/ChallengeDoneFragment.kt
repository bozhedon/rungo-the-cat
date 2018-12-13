package com.myrungo.rungo.challenge.done

import android.annotation.SuppressLint
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.fragment_challenge_done.*
import toothpick.Toothpick

class ChallengeDoneFragment : BaseFragment(), ChallengeDoneView {


    override val layoutRes = R.layout.fragment_challenge_done

    @InjectPresenter
    lateinit var presenter: ChallengeDonePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.PLAY)
        .getInstance(ChallengeDonePresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        home_button.setOnClickListener {presenter.onHomeClicked()}
    }

    @SuppressLint("ResourceAsColor")
    override fun showLayout(resultState: Int) {
        done_result_layout.setBackgroundResource(
            when (resultState) {
                0 -> R.color.colorGreen
                1 -> R.color.run_active_background
                else -> R.color.colorBrown
            }
        )

        challenge_done_cat.setImageResource(
            when (resultState){
                0 -> R.drawable.head_happy_vector
                1 -> R.drawable.head_common_vector
                else -> R.drawable.head_sad_vector
            }
        )

        when (resultState) {
            0 -> {
                challenge_done_title.text = getString(R.string.challenge_done)
                challenge_done_title.setTextColor(R.color.colorGreen)
            }
            1 -> {
                challenge_done_title.text = getString(R.string.challenge_good_job)
                challenge_done_title.setTextColor(R.color.run_active_background)
            }
            else -> {
                challenge_done_title.text = getString(R.string.challenge_failed)
                challenge_done_title.setTextColor(R.color.colorBrown)
            }
        }
    }

    override fun showDistance(curDistanceResult: String, challengeDistance: String) {
        done_current_distance.text = curDistanceResult
        done_distance_divider.visible(challengeDistance.isNotEmpty())
        done_challenge_distance.text = challengeDistance
        done_challenge_distance.visible(challengeDistance.isNotEmpty())
    }

    override fun showTime(curTimeResult: String) {
        done_timer.text = curTimeResult
    }

    override fun showSpeed(curSpeedResult: String, avgSpeedResult: String) {
        done_current_speed.text = curSpeedResult
        done_average_speed.text = avgSpeedResult
    }

    override fun showGift() {

    }

}