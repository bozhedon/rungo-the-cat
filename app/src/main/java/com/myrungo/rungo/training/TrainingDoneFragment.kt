package com.myrungo.rungo.training

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import kotlinx.android.synthetic.main.fragment_training_done.*
import toothpick.Toothpick

class TrainingDoneFragment : BaseFragment(), TrainingDoneView{

    override val layoutRes = R.layout.fragment_training_done

    @InjectPresenter
    lateinit var presenter: TrainingDonePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.PLAY)
        .getInstance(TrainingDonePresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        training_home_button.setOnClickListener {presenter.onHomeClicked()}
    }

    override fun showDistance(trainingDistanceResult: String) {
        training_current_distance.text = trainingDistanceResult
    }

    override fun showTime(curTimeResult: String) {
        training_done_timer.text = curTimeResult
    }

    override fun showSpeed(curSpeedResult: String, avgSpeedResult: String) {
        training_average_speed.text = curSpeedResult
        training_current_speed.text = curSpeedResult
    }

}