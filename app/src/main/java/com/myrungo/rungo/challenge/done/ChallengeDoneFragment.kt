package com.myrungo.rungo.challenge.done

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.challenge.accept.ChallengeAcceptFragment
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
                else -> R.color.colorBrown
            }
        )

        challenge_done_cat.setImageResource(
            when (resultState){
                0 -> R.drawable.head_happy_vector
                else -> R.drawable.head_sad_vector
            }
        )

        when (resultState) {
            0 -> {
                challenge_done_title.text = getString(R.string.challenge_done)
                challenge_done_title.setTextColor(R.color.colorGreen)
                done_gift_title.text = getString(R.string.take_your_gift)
                challenge_done_fail.visibility = View.INVISIBLE
            }
            else -> {
                challenge_done_title.text = getString(R.string.challenge_failed)
                challenge_done_title.setTextColor(R.color.colorBrown)
                done_gift_title.text = getString(R.string.try_once_more)
                challenge_done_fail.visibility = View.VISIBLE
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

    override fun showGift(resId: Int) {
        when (resId) {
            2131165390 -> challenge_done_gift.setImageResource(R.drawable.karate_cat_kimono_vector)
            2131165281 -> challenge_done_gift.setImageResource(R.drawable.bad_cat_jacket_vector)
            else -> challenge_done_gift.setImageResource(R.drawable.bussiness_cat_cloth_vector)
        }
    }
}