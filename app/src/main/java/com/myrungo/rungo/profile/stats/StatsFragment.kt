package com.myrungo.rungo.profile.stats

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.profile.stats.models.UserTimeIntervalsInfo
import kotlinx.android.synthetic.main.fragment_stats.*
import toothpick.Toothpick

class StatsFragment : BaseFragment(), StatsView {
    override val layoutRes = R.layout.fragment_stats
    private val tabId: Int?
        get() = arguments?.getInt(ARG_TAB)

    @InjectPresenter
    lateinit var presenter: StatsPresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(StatsPresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        stats_this_time_title.text = when (tabId) {
            0 -> getString(R.string.stats_this_week)
            1 -> getString(R.string.stats_this_month)
            2 -> getString(R.string.stats_this_year)
            else -> getString(R.string.stats_this_week)
        }

        stats_last_time_title.text = when (tabId) {
            0 -> getString(R.string.stats_last_week)
            1 -> getString(R.string.stats_last_month)
            2 -> getString(R.string.stats_last_year)
            else -> getString(R.string.stats_last_week)
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.getInfoFor(tabId)
    }

    override fun showInfo(trainings: UserTimeIntervalsInfo) {
        stats_this_time_distance.text = trainings.currentTime.totalDistance.toString().take(5)
        stats_this_time_avg_speed.text = trainings.currentTime.totalAverageSpeed.toString().take(5)
        stats_this_time_trainings.text = trainings.currentTime.totalNumberTrainings.toString()

        stats_last_time_distance.text = trainings.previousTime.totalDistance.toString().take(5)
        stats_last_time_avg_speed.text = trainings.previousTime.totalAverageSpeed.toString().take(5)
        stats_last_time_trainings.text = trainings.previousTime.totalNumberTrainings.toString()
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            activity?.let {
                Snackbar.make(
                    it.findViewById<View>(android.R.id.content),
                    message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val ARG_TAB = "sf_tab"
        fun newInstance(tab: Int) = StatsFragment()
            .apply {
                arguments = Bundle().apply { putInt(ARG_TAB, tab) }
            }
    }
}