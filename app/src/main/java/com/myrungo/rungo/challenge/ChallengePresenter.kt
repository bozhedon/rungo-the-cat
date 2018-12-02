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
                ChallengeItem(0, 5, 30, R.drawable.bad_cat_jacket),
                ChallengeItem(1, 10, 40, R.drawable.bussiness_cat_cloth),
                ChallengeItem(2, 15, 130, R.drawable.karate_cat_kimono)
            )
        )
    }

    fun onChallengeClicked(challenge: ChallengeItem) {
        viewState.showAcceptDialog(challenge)
    }

    fun onBackPressed() = navigation.open(0)
}