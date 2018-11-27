package com.myrungo.rungo.profile.stats

import android.os.Bundle
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : BaseFragment() {
    override val layoutRes = R.layout.fragment_stats
    private val tabId get() = arguments?.getInt(ARG_TAB)

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

    companion object {
        private const val ARG_TAB = "sf_tab"
        fun newInstance(tab: Int) = StatsFragment()
            .apply {
                arguments = Bundle().apply { putInt(ARG_TAB, tab) }
            }
    }
}