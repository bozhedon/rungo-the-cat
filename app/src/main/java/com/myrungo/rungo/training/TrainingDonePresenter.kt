package com.myrungo.rungo.training

import com.arellomobile.mvp.InjectViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.utils.constants.trainingsCollection
import com.myrungo.rungo.utils.constants.usersCollection
import ru.terrakok.cicerone.Router
import java.lang.RuntimeException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class TrainingDonePresenter @Inject constructor(
    private val router: Router
) : BasePresenter<TrainingDoneView>() {

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser
            ?: throw RuntimeException("")

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val currentUserTrainingCollection
        get() = currentUserDocument.collection(trainingsCollection)
            .orderBy("endTime")


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUserTime()
        getAverageSpeed()
        getDistance()
    }

    private fun getDistance() {
        val task = currentUserTrainingCollection.get()
        waitForAnyResult(task)
        val currentUserTrainingCollectionSnapshot = task.result
        val decimalFormat = DecimalFormat("#.0", DecimalFormatSymbols(Locale.US))
        val curDistance = decimalFormat.format(currentUserTrainingCollectionSnapshot!!.documents.last().get("distance")).toDouble()
        viewState.showDistance(curDistance.toString())
    }

    fun onHomeClicked(){
        router.navigateTo(Screens.MainFlow)
    }

    private fun getUserTime(){
        val task = currentUserTrainingCollection.get()
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

    private fun getAverageSpeed(){
        val task = currentUserTrainingCollection.get()
        waitForAnyResult(task)
        val currentUserTrainingCollectionSnapshot = task.result
        val decimalFormat = DecimalFormat("#.0", DecimalFormatSymbols(Locale.US))
        val avgSpeed = decimalFormat.format(currentUserTrainingCollectionSnapshot!!.documents.last().get("averageSpeed")).toDouble()
        val curSpeed = (avgSpeed * 3.6) / 2
        viewState.showSpeed(curSpeed.toString(), avgSpeed.toString())
    }
}