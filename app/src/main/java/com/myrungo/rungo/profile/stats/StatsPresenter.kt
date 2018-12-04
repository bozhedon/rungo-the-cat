package com.myrungo.rungo.profile.stats

import com.arellomobile.mvp.InjectViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.constants.challengesCollection
import com.myrungo.rungo.constants.trainingsCollection
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.ResourceManager
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
    private val resourceManager: ResourceManager,
    private val navigation: MainNavigationController,
    private val catController: CatController,
    private val authData: AuthHolder,
    private val networkManager: NetworkManager
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
            val calendarTimeInterval = when (timeField) {
                TimeInterval.WEEK -> Calendar.WEEK_OF_YEAR
                TimeInterval.MONTH -> Calendar.MONTH
                TimeInterval.YEAR -> Calendar.YEAR
            }

            val currentCalendar = Calendar.getInstance()
            val currentTimeInterval = currentCalendar.get(calendarTimeInterval)
            currentCalendar.add(calendarTimeInterval, -1)
            val previousTimeInterval = currentCalendar.get(calendarTimeInterval)

            var currentTimeIntervalTotalDistance = 0.0
            var previousTimeIntervalTotalDistance = 0.0

            var currentTimeIntervalTotalNumberTrainings: Long = 0
            var previousTimeIntervalTotalNumberTrainings: Long = 0

            var currentTimeIntervalAverageSpeed = 0.0
            var previousTimeIntervalAverageSpeed = 0.0

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
                    currentTimeIntervalTotalDistance += training.distance
                    currentTimeIntervalAverageSpeed += training.averageSpeed
                } else if (trainingWasOnPreviousTimeInterval) {
                    previousTimeIntervalTotalNumberTrainings++
                    previousTimeIntervalTotalDistance += training.distance
                    previousTimeIntervalAverageSpeed += training.averageSpeed
                }
            }

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
                    currentTimeIntervalTotalDistance += challenge.distance
                    currentTimeIntervalAverageSpeed += challenge.averageSpeed
                } else if (trainingWasOnPreviousTimeInterval) {
                    previousTimeIntervalTotalDistance += challenge.distance
                    previousTimeIntervalAverageSpeed += challenge.averageSpeed
                }
            }

            val currentTimeIntervalInfo = UserTimeIntervalInfo(
                currentTimeIntervalTotalDistance,
                currentTimeIntervalTotalNumberTrainings,
                currentTimeIntervalAverageSpeed
            )

            val previousTimeIntervalInfo = UserTimeIntervalInfo(
                previousTimeIntervalTotalDistance,
                previousTimeIntervalTotalNumberTrainings,
                previousTimeIntervalAverageSpeed
            )

            emitter.onSuccess(
                UserTimeIntervalsInfo(
                    currentTimeIntervalInfo,
                    previousTimeIntervalInfo
                )
            )
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
            ?: throw RuntimeException("User must sign in app")

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val currentUserTrainingsCollection
        get() = currentUserDocument.collection(trainingsCollection)

    private val currentUserTrainings: List<Training>
        get() {
            if (!networkManager.isConnectedToInternet) {
                viewState.showMessage("Необходимо интернет соединение")
                router.navigateTo(Screens.MainFlow)

                throw RuntimeException("Необходимо интернет соединение")
            }

            val task = currentUserTrainingsCollection.get()

            waitForAnyResult(task)

            task.exception?.let { report(it); throw it }

            val result = task.result ?: return emptyList()

            val userTrainings = mutableListOf<Training>()

            for (document in result.documents) {
                if (document != null) {
                    try {
                        document.toObject(Training::class.java)?.let { userTrainings += it }
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
                viewState.showMessage("Необходимо интернет соединение")
                router.navigateTo(Screens.MainFlow)

                throw RuntimeException("Необходимо интернет соединение")
            }

            val task = currentUserChallengesCollection.get()

            waitForAnyResult(task)

            task.exception?.let { report(it); throw it }

            val result = task.result ?: return emptyList()

            val userChallenges = mutableListOf<Challenge>()

            for (document in result.documents) {
                if (document != null) {
                    try {
                        document.toObject(Challenge::class.java)?.let { userChallenges += it }
                    } catch (exception: Exception) {
                        report(exception)
                        continue
                    }
                }
            }

            return userChallenges
        }

}