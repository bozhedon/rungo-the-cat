package com.myrungo.rungo.challenge

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.list.HeaderItem
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.ResourceManager
import javax.inject.Inject

@InjectViewState
class ChallengePresenter @Inject constructor(
    private val navigation: MainNavigationController,
    private val resourceManager: ResourceManager
) : BasePresenter<ChallengeView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showData(
            listOf(
                HeaderItem(resourceManager.getString(R.string.choose_challenge)),
                ChallengeItem(0, "5 км", "00:30", R.drawable.bad_cat_jacket),
                ChallengeItem(1, "10 км", "00:40", R.drawable.bussiness_cat_cloth),
                ChallengeItem(2, "15 км", "01:30", R.drawable.karate_cat_kimono),
                ChallengeItem(3, "8 км", "00:40", R.drawable.normal_cat_sportsuniform)
            )
        )
    }

    fun onChallengeClicked(challenge: ChallengeItem) {

    }

    fun onBackPressed() = navigation.open(0)
}