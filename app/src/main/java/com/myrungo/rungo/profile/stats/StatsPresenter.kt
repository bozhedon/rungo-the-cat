package com.myrungo.rungo.profile.stats

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Screens
import com.myrungo.rungo.utils.constants.challengesCollection
import com.myrungo.rungo.utils.constants.trainingsCollection
import com.myrungo.rungo.utils.constants.usersCollection
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.profile.stats.models.*
import com.myrungo.rungo.profile.stats.models.TimeInterval.*
import com.myrungo.rungo.utils.NetworkManager
import io.reactivex.Single
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

@InjectViewState
class StatsPresenter @Inject constructor(
    private val router: Router,
    private val schedulersProvider: SchedulersProvider,
    private val networkManager: NetworkManager,
    private val context: Context
) : BasePresenter<StatsView>() {

    fun getInfoFor(tabId: Int?) {
        val calculateInfoFor = when (tabId) {
            0 -> calculateInfoFor(WEEK)
            1 -> calculateInfoFor(MONTH)
            2 -> calculateInfoFor(YEAR)
            else -> calculateInfoFor(WEEK)
        }

        calculateInfoFor
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .doOnSubscribe { viewState.showProgress(true) }
            .doAfterTerminate { viewState.showProgress(false) }
            .subscribe(
                {
                    viewState.showInfo(it)
                },
                {
                    report(it)
                    viewState.showMessage(it.message)
                }
            ).connect()
    }

    private fun calculateInfoFor(timeField: TimeInterval): Single<UserTimeIntervalsInfo> =
        Single.create { emitter ->
            try {
                val calendarTimeInterval = when (timeField) {
                    TimeInterval.WEEK -> Calendar.WEEK_OF_YEAR
                    TimeInterval.MONTH -> Calendar.MONTH
                    TimeInterval.YEAR -> Calendar.YEAR
                }

                val currentCalendar = Calendar.getInstance()
                val currentTimeInterval = currentCalendar.get(calendarTimeInterval)
                currentCalendar.add(calendarTimeInterval, -1)
                val previousTimeInterval = currentCalendar.get(calendarTimeInterval)

                var currentTimeIntervalTotalDistanceInKm = 0.0
                var previousTimeIntervalTotalDistanceInKm = 0.0

                var currentTimeIntervalTotalNumberTrainings: Long = 0
                var previousTimeIntervalTotalNumberTrainings: Long = 0

                var currentTimeIntervalAverageSpeedInKmH = 0.0
                var previousTimeIntervalAverageSpeedInKmH = 0.0

                for (training in currentUserTrainings) {
                    val trainingWasOnCurrentTimeInterval = wasTrainingOnThis(
                        currentTimeInterval,
                        training.startTime,
                        training.endTime,
                        calendarTimeInterval
                    )

                    val trainingWasOnPreviousTimeInterval = wasTrainingOnThis(
                        previousTimeInterval,
                        training.startTime,
                        training.endTime,
                        calendarTimeInterval
                    )

                    if (trainingWasOnCurrentTimeInterval) {
                        currentTimeIntervalTotalNumberTrainings++
                        currentTimeIntervalTotalDistanceInKm += training.distanceInKm
                        currentTimeIntervalAverageSpeedInKmH += training.averageSpeedInKmH
                    } else if (trainingWasOnPreviousTimeInterval) {
                        previousTimeIntervalTotalNumberTrainings++
                        previousTimeIntervalTotalDistanceInKm += training.distanceInKm
                        previousTimeIntervalAverageSpeedInKmH += training.averageSpeedInKmH
                    }
                }

                var currentTimeIntervalTotalNumberChallenges: Long = 0
                var previousTimeIntervalTotalNumberChallenges: Long = 0

                for (challenge in currentUserChallenges) {
                    val trainingWasOnCurrentTimeInterval = wasTrainingOnThis(
                        currentTimeInterval,
                        challenge.startTime,
                        challenge.endTime,
                        calendarTimeInterval
                    )

                    val trainingWasOnPreviousTimeInterval = wasTrainingOnThis(
                        previousTimeInterval,
                        challenge.startTime,
                        challenge.endTime,
                        calendarTimeInterval
                    )

                    if (trainingWasOnCurrentTimeInterval) {
                        currentTimeIntervalTotalNumberChallenges++
                        currentTimeIntervalTotalDistanceInKm += challenge.distanceInKm
                        currentTimeIntervalAverageSpeedInKmH += challenge.averageSpeedInKmH
                    } else if (trainingWasOnPreviousTimeInterval) {
                        previousTimeIntervalTotalNumberChallenges++
                        previousTimeIntervalTotalDistanceInKm += challenge.distanceInKm
                        previousTimeIntervalAverageSpeedInKmH += challenge.averageSpeedInKmH
                    }
                }

                val currentTimeIntervalInfo =
                    run {
                        val trainingsAndChallengesNumberInCurrentTimeInterval =
                            currentTimeIntervalTotalNumberTrainings +
                                    currentTimeIntervalTotalNumberChallenges

                        val totalAverageSpeedInCurrentTimeInterval =
                            if (trainingsAndChallengesNumberInCurrentTimeInterval == 0L) {
                                0.0
                            } else {
                                currentTimeIntervalAverageSpeedInKmH /
                                        trainingsAndChallengesNumberInCurrentTimeInterval
                            }

                        UserTimeIntervalInfo(
                            currentTimeIntervalTotalDistanceInKm,
                            currentTimeIntervalTotalNumberTrainings,
                            totalAverageSpeedInCurrentTimeInterval
                        )
                    }

                val previousTimeIntervalInfo =
                    run {
                        val trainingsAndChallengesNumberInPreviousTimeInterval =
                            previousTimeIntervalTotalNumberTrainings +
                                    previousTimeIntervalTotalNumberChallenges

                        val totalAverageSpeedInPreviousTimeInterval =
                            if (trainingsAndChallengesNumberInPreviousTimeInterval == 0L) {
                                0.0
                            } else {
                                previousTimeIntervalAverageSpeedInKmH /
                                        trainingsAndChallengesNumberInPreviousTimeInterval
                            }

                        UserTimeIntervalInfo(
                            previousTimeIntervalTotalDistanceInKm,
                            previousTimeIntervalTotalNumberTrainings,
                            totalAverageSpeedInPreviousTimeInterval
                        )
                    }

                emitter.onSuccess(
                    UserTimeIntervalsInfo(
                        currentTimeIntervalInfo,
                        previousTimeIntervalInfo
                    )
                )
            } catch (e: Exception) {
                report(e)
                emitter.onError(e)
            }
        }

    private fun wasTrainingOnThis(
        timeInterval: Int,
        startTime: Long,
        endTime: Long,
        calendarTimeInterval: Int
    ): Boolean {
        val startWeek = getTimeIntervalSerialNumber(startTime, calendarTimeInterval)

        val endWeek = getTimeIntervalSerialNumber(endTime, calendarTimeInterval)

        return startWeek == timeInterval && endWeek == timeInterval
    }

    private fun getTimeIntervalSerialNumber(time: Long, calendarTimeInterval: Int): Int {
        val date = Date(time)
        val calendar = Calendar.getInstance()!!
        calendar.time = date

        return calendar.get(calendarTimeInterval)
    }

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser
            ?: throw RuntimeException(context.getString(R.string.user_must_sign_in))

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val currentUserTrainingsCollection
        get() = currentUserDocument.collection(trainingsCollection)

    private val currentUserTrainings: List<Training>
        get() {
            if (!networkManager.isConnectedToInternet) {
                viewState.showMessage(context.getString(R.string.internet_connection_required))
                router.navigateTo(Screens.MainFlow)

                throw RuntimeException(context.getString(R.string.internet_connection_required))
            }

            val task = currentUserTrainingsCollection.get()

            waitForAnyResult(task)

            task.exception?.let { report(it); throw it }

            val result = task.result ?: return emptyList()

            val userTrainings = mutableListOf<Training>()

            for (document in result.documents) {
                if (document != null) {
                    try {
                        val training = document.toObject(Training::class.java)
                        training?.let { userTrainings += it }
                    } catch (exception: Exception) {
                        report(exception)
                        continue
                    }
                }
            }

            return userTrainings
        }

    private val currentUserChallengesCollection
        get() = currentUserDocument.collection(challengesCollection)

    private val currentUserChallenges: List<Challenge>
        get() {
            if (!networkManager.isConnectedToInternet) {
                viewState.showMessage(context.getString(R.string.internet_connection_required))
                router.navigateTo(Screens.MainFlow)

                throw RuntimeException(context.getString(R.string.internet_connection_required))
            }

            val task = currentUserChallengesCollection.get()

            waitForAnyResult(task)

            task.exception?.let { report(it); throw it }

            val currentUserChallengesCollectionSnapshot = task.result ?: return emptyList()

            val userChallenges = mutableListOf<Challenge>()

            for (currentUserChallenge in currentUserChallengesCollectionSnapshot.documents) {
                if (currentUserChallenge != null) {
                    try {
                        val challenge = currentUserChallenge.toObject(Challenge::class.java)
                        challenge?.let { userChallenges += it }
                    } catch (exception: Exception) {
                        report(exception)
                        continue
                    }
                }
            }

            return userChallenges
        }

}