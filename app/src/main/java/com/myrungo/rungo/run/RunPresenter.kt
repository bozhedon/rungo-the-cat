package com.myrungo.rungo.run

import android.annotation.SuppressLint
import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.constants.challengesCollection
import com.myrungo.rungo.constants.trainingsCollection
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.profile.stats.models.Challenge
import com.myrungo.rungo.profile.stats.models.Training
import com.myrungo.rungo.toTime
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class RunPresenter @Inject constructor(
    private val challenge: ChallengeItem,
    private val router: Router,
    private val locationProvider: ReactiveLocationProvider,
    private val catController: CatController,
    private val challengeController: ChallengeController,
    private val navigationController: MainNavigationController,
    private val schedulers: SchedulersProvider,
    private val authData: AuthHolder
) : BasePresenter<RunView>() {
    private val req = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(LOCATION_UPDATE_INTERVAL)

    private var currentTab = 0
    private var isRun = false
    private var timerDisposable: Disposable? = null
    private var initTime = 0
    private var isComplete = false
    private var timeOut = false
    private var currentLocation: Location? = null
    private var currentDistance = 0.0
    private var lastDistance = 0.0
    private var lastTime = 0

    private var totalDistance = 0.0

    private var startTime: Long = 0
    private var endTime: Long = 0

    private var avgSpeed = 0.0

    private var curSpeed = 0.0

    private val challengeTime =
        if (challenge.id != ChallengeController.EMPTY.id) "${challenge.time / 100}:${challenge.time % 100}"
        else ""

    private val challengeDistance =
        if (challenge.id != ChallengeController.EMPTY.id) challenge.distance.toString()
        else ""

    @SuppressLint("MissingPermission")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        locationProvider.getUpdatedLocation(req)
            .filter { isRun }
            .observeOn(schedulers.ui())
            .doOnSubscribe { viewState.showDistance("0,0", challengeDistance) }
            .subscribe(
                { location ->
                    val temp = currentLocation
                    currentLocation = location

                    val distance = if (temp != null) currentLocation?.distanceTo(temp)?.toDouble()
                        ?: 0.0 else 0.0
                    lastDistance = distance
                    lastTime = initTime
                    currentDistance += if (distance > 0) distance / 1000 else 0.0
                    viewState.showDistance("%.1f".format(currentDistance), challengeDistance)

                    authData.distance = authData.distance + currentDistance
                    totalDistance += currentDistance
                },
                { Timber.e(it) }
            )
            .connect()

        catController.skinState
            .subscribe(
                { viewState.showSkin(it) },
                { Timber.e(it) }
            )
            .connect()

        onStartClicked()
    }

    private fun startTimer() {
        timerDisposable?.dispose()

        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .doOnSubscribe {
                viewState.showTime(
                    if (initTime != 0) initTime.toTime() else "00:00:00",
                    challengeTime
                )
                viewState.showSpeed(0.0, 0.0)

                startTime = System.currentTimeMillis()
            }
            .subscribe(
                {
                    initTime++
                    viewState.showTime(initTime.toTime(), challengeTime)

                    val h = challenge.time / 100
                    val m = challenge.time % 100

                    if (challenge.id != ChallengeController.EMPTY.id) {
                        isComplete =
                                (currentDistance >= challenge.distance && initTime <= m * 60 + h * 3600)
                        timeOut = initTime >= m * 60 + h * 3600
                    }

                    curSpeed =
                            if ((initTime - lastTime).toFloat() / 3600 > 0) (lastDistance / 1000) / ((initTime - lastTime).toDouble() / 3600) else 0.0
                    avgSpeed =
                            if (initTime.toFloat() / 3600 > 0) currentDistance / (initTime.toDouble() / 3600) else 0.0

                    viewState.showSpeed(curSpeed, avgSpeed)
                },
                { Timber.e(it) }
            )
    }

    fun onTabClicked(position: Int) {
        if (position == currentTab) return

        viewState.showMap(position == 1)
        currentTab = position
    }

    fun onStartClicked() {
        isRun = !isRun
        viewState.run(isRun)
        if (isRun) {
            startTimer()
        } else {
            endTime = System.currentTimeMillis()
            timerDisposable?.dispose()
        }
    }

    fun onStopClicked() {
        if (!isComplete && challenge.id != ChallengeController.EMPTY.id && !timeOut) {
            viewState.showDialog(
                title = "Нет больше сил?",
                msg = "Твой вызов еще не выполнен!\nЗакончить тренировку?",
                tag = DIALOG_TAG
            )
        } else if (!isComplete && !timeOut && challenge.id == ChallengeController.EMPTY.id) {
            viewState.showDialog(
                title = "Закончить тренировку?",
                msg = "",
                tag = DIALOG_TAG
            )
        } else if (isComplete && challenge.id != ChallengeController.EMPTY.id) {
            viewState.showDialog(
                title = "Поздравляем!",
                msg = "Молодец! Ты выполнил вызов!\n" + "Закончить тренировку?",
                tag = DIALOG_TAG
            )
        } else {
            viewState.showDialog(
                title = "Вызов не выполнен",
                msg = "Продолжить как тренировку?",
                tag = DIALOG_TAG
            )
        }
    }

    fun exit() {
        saveTotalDistanceToDB()

        val isItChallenge = challenge.id != ChallengeController.EMPTY.id

        if (/*isComplete &&*/ isItChallenge) {
            challengeController.finishChallenge(challenge)

            ChallengeController.getAward(challenge)?.let { award ->
                saveChallengeToSP(award)

                saveChallengeToDB(award)
            }

            navigationController.open(1)
            return
        } else if (!isItChallenge) {
            saveTrainingToDB()
            return
        }

        router.exit()
    }

    private fun saveTotalDistanceToDB() {

    }

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser!!

    private val currentUserChallengesCollection
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)
            .collection(challengesCollection)

    private val currentUserTrainingsCollection
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)
            .collection(trainingsCollection)

    private fun saveTrainingToDB() {
        val trainingInfo = Training(
            distance = totalDistance,
            startTime = startTime,
            endTime = endTime,
            averageSpeed = avgSpeed
        )

        RxFirestore.addDocument(currentUserTrainingsCollection, trainingInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { router.exit() },
                {
                    viewState.showMessage(it.message)
                    report(it)
                    router.exit()
                }
            )
            .connect()
    }

    private fun saveChallengeToSP(award: CatView.Skins) {
        val availableSkins = mutableListOf<CatView.Skins>()
        availableSkins.addAll(authData.availableSkins)
        availableSkins.add(award)

        authData.availableSkins = availableSkins
    }

    private fun saveChallengeToDB(award: CatView.Skins) {
        val hour = initTime / 3600
        val minutes = initTime / 60

        val challengeInfo = Challenge(
            distance = totalDistance,
            hour = hour,
            id = challenge.id,
            imgURL = "",
            isComplete = isComplete,
            minutes = minutes,
            reward = award.name,
            startTime = startTime,
            endTime = endTime,
            averageSpeed = avgSpeed
        )

        RxFirestore.addDocument(currentUserChallengesCollection, challengeInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { router.exit() },
                {
                    Timber.e(it)
                    report(it)
                    viewState.showMessage(it.message)
                    router.exit()
                }
            )
            .connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 100L
        private const val DIALOG_TAG = "rp_dialog_tag"
    }
}