package com.myrungo.rungo.challenge.done

import com.arellomobile.mvp.InjectViewState
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.utils.constants.challengesCollection
import com.myrungo.rungo.utils.constants.trainingsCollection
import com.myrungo.rungo.utils.constants.usersCollection
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ChallengeDonePresenter @Inject constructor(
    challengeItem: ChallengeItem,
    private val router: Router
) : BasePresenter<ChallengeDoneView>() {


    private val challengeDistance =
        if (challengeItem.id != ChallengeController.EMPTY.id) challengeItem.distance.toString()
        else ""

    private val challengeGift = challengeItem.awardRes

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser
            ?: throw RuntimeException("")

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val currentUserChallengeCollection
        get() = currentUserDocument.collection(challengesCollection)
            .orderBy("endTime")

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUserTime()
        getAverageSpeed()
        getDistance()
        getLayout()
        getGift(challengeGift)
    }

    fun onHomeClicked() {
        router.navigateTo(Screens.MainFlow)
    }

    private fun getGift(resId : Int) {
        viewState.showGift(resId)
    }

    private fun getUserTime() {
        val task = currentUserChallengeCollection.get()
        waitForAnyResult(task)
        val currentUserChallengesCollectionSnapshot = task.result
        val endTime = currentUserChallengesCollectionSnapshot!!.documents.last().get("endTime").toString()
        val startTime = currentUserChallengesCollectionSnapshot.documents.last().get("startTime").toString()
        val resultTime = endTime.toLong() - startTime.toLong()
        val hour = TimeUnit.MILLISECONDS.toHours(resultTime)
        val minute = TimeUnit.MILLISECONDS.toMinutes(resultTime)
        val second = TimeUnit.MILLISECONDS.toSeconds(resultTime)
        viewState.showTime(String.format("%02d:%02d:%02d", hour, minute, second))
    }

    private fun getAverageSpeed() {
        val task = currentUserChallengeCollection.get()
        waitForAnyResult(task)
        val currentUserChallengesCollectionSnapshot = task.result
        val decimalFormat = DecimalFormat("#.0", DecimalFormatSymbols(Locale.US))
        val avgSpeed =
            decimalFormat.format(currentUserChallengesCollectionSnapshot!!.documents.last().get("averageSpeed"))
                .toDouble()
        val curSpeed = (avgSpeed * 3.6) / 2
        viewState.showSpeed(curSpeed.toString(), avgSpeed.toString())
    }

    private fun getDistance() {
        val task = currentUserChallengeCollection.get()
        waitForAnyResult(task)
        val currentUserChallengesCollectionSnapshot = task.result
        val decimalFormat = DecimalFormat("#.0", DecimalFormatSymbols(Locale.US))
        val curDistance =
            decimalFormat.format(currentUserChallengesCollectionSnapshot!!.documents.last().get("distance")).toDouble()
        viewState.showDistance(curDistance.toString(), challengeDistance)
    }

    private fun getLayout() {
        val task = currentUserChallengeCollection.get()
        waitForAnyResult(task)
        val currentUserChallengeCollectionSnapshot = task.result
        val isComplete: Boolean =
            currentUserChallengeCollectionSnapshot!!.documents.last().get("isComplete").toString().toBoolean()

        if (isComplete) {
            viewState.showLayout(0)
        } else {
            viewState.showLayout(1)
        }
    }
}