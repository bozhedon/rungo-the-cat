package com.myrungo.rungo.challenge

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.list.HeaderItem
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.ResourceManager
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChallengePresenter @Inject constructor(
    private val navigation: MainNavigationController,
    private val resourceManager: ResourceManager,
    private val challengeController: ChallengeController,
    private val authData: AuthHolder
) : BasePresenter<ChallengeView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        challengeController.state
            .subscribe(
                {
                    viewState.showData(
                        listOf(
                            HeaderItem(resourceManager.getString(R.string.choose_challenge)),
                            ChallengeItem(0, 5, 30, R.drawable.bad_cat_jacket, if (it.id == 0) 1 else 0),
                            ChallengeItem(1, 10, 40, R.drawable.bussiness_cat_cloth, if (it.id == 1) 1 else 0),
                            ChallengeItem(2, 15, 130, R.drawable.karate_cat_kimono, if (it.id == 2) 1 else 0)
                        )
                    )
                },
                { Timber.e(it) }
            )
            .connect()

        viewState.showData(
            listOf(
                HeaderItem(resourceManager.getString(R.string.choose_challenge)),
                ChallengeItem(0, 5, 30, R.drawable.bad_cat_jacket, if (authData.availableSkins.find { it == CatView.Skins.BAD } != null) 1 else 0),
                ChallengeItem(1, 10, 40, R.drawable.bussiness_cat_cloth, if (authData.availableSkins.find { it == CatView.Skins.BUSINESS } != null) 1 else 0),
                ChallengeItem(2, 15, 130, R.drawable.karate_cat_kimono, if (authData.availableSkins.find { it == CatView.Skins.KARATE } != null) 1 else 0)
            )
        )
    }

    fun onChallengeClicked(challenge: ChallengeItem) {
        if (challenge.isComplete == 1) return

        viewState.showAcceptDialog(challenge)
    }

    fun onBackPressed() = navigation.open(0)
}