package com.myrungo.rungo.challenge

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.challenge.accept.ChallengeAcceptFragment
import com.myrungo.rungo.list.HeaderAdapterDelegate
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.fragment_challenge.*
import toothpick.Toothpick

class ChallengeFragment : BaseFragment(), ChallengeView {
    override val layoutRes = R.layout.fragment_challenge
    private val adapter by lazy { ChallengeAdapter() }

    @InjectPresenter
    lateinit var presenter: ChallengePresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.APP)
        .getInstance(ChallengePresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        challenge_recycler_view.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        presenter.onStart()
    }

    override fun showData(data: List<Any>) {
        postViewAction { adapter.setData(data) }
    }

    override fun showProgress(show: Boolean) {
        challenge_recycler_view.visible(!show)
        challenge_progress.visible(show)
    }

    override fun showAcceptDialog(challenge: ChallengeItem) {
        if (childFragmentManager.findFragmentByTag(ACCEPT_TAG) == null) {
            ChallengeAcceptFragment.newInstance(challenge).show(childFragmentManager, ACCEPT_TAG)
            childFragmentManager.executePendingTransactions()
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    private inner class ChallengeAdapter : ListDelegationAdapter<MutableList<Any>>() {
        init {
            items = mutableListOf()

            delegatesManager
                .addDelegate(HeaderAdapterDelegate())
                .addDelegate(ChallengeAdapterDelegate { presenter.onChallengeClicked(it) })
        }

        fun setData(data: List<Any>) {
            items.clear()
            items.addAll(data)
            //todo use DiffUtils
            notifyDataSetChanged()
        }
    }

    companion object {
        private const val ACCEPT_TAG = "cf_accept"
    }
}