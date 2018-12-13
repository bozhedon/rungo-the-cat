package com.myrungo.rungo.challenge.done

import com.arellomobile.mvp.InjectViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.utils.constants.challengesCollection
import com.myrungo.rungo.utils.constants.trainingsCollection
import com.myrungo.rungo.utils.constants.usersCollection
import ru.terrakok.cicerone.Router
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

    private val currentUserTrainingCollection
        get() = currentUserDocument.collection(trainingsCollection)
            .orderBy("endTime")

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUserTime()
        getAverageSpeed()
        getDistance()
        getLayout()
    }

    fun onHomeClicked() {
        router.navigateTo(Screens.MainFlow)
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

    private fun getGift() {

    }

    private fun getLayout() {
        val isChallengeState = true
        val task = currentUserChallengeCollection.get()
        val trainingTask = currentUserTrainingCollection.get()
        waitForAnyResult(trainingTask)
        waitForAnyResult(task)
        val currentUserChallengeCollectionSnapshot = task.result
        val isComplete: Boolean =
            currentUserChallengeCollectionSnapshot!!.documents.last().get("isComplete").toString().toBoolean()

        currentUserChallengeCollectionSnapshot.documents.last().get("isComplete").toString().toBoolean()

        if (isComplete && isChallengeState) {
            viewState.showLayout(0)
        }
        if (!isComplete && isChallengeState) {
            viewState.showLayout(2)
        } else if (!isChallengeState) {
            viewState.showLayout(1)
        }
    }
}