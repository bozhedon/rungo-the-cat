package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
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
import com.myrungo.rungo.constants.userTotalDistanceKey
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.model.database.AppDatabase
import com.myrungo.rungo.model.location.TraininigListener
import com.myrungo.rungo.profile.stats.models.Challenge
import com.myrungo.rungo.profile.stats.models.Training
import com.myrungo.rungo.toTime
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class RunPresenter @Inject constructor(
    private val challenge: ChallengeItem,
    private val router: Router,
    private val catController: CatController,
    private val challengeController: ChallengeController,
    private val navigationController: MainNavigationController,
    private val schedulers: SchedulersProvider,
    private val authData: AuthHolder,
    private val database: AppDatabase,
    private val traininigListener: TraininigListener
) : BasePresenter<RunView>() {

    private var currentTab = 0
    private var timerDisposable: Disposable? = null
    /**в секундах*/
    private var timeInSeconds = 0
    private var isComplete = false
    private var timeOut = false
    /**в метрах*/
    private var distanceInMeters = 0.0

    private var startTime: Long = 0
    private var endTime: Long = 0

    private val challengeTime =
        if (challenge.id != ChallengeController.EMPTY.id) "${challenge.time / 100}:${challenge.time % 100}"
        else ""

    private val challengeDistance =
        if (challenge.id != ChallengeController.EMPTY.id) challenge.distance.toString()
        else ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        traininigListener.isRun = false

        database.locationDao.listenLastLocation()
            .observeOn(schedulers.ui())
            .subscribe(
                { location ->
                    viewState.drawRoute(location)
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

        traininigListener.listen()
            .doOnSubscribe {
                viewState.showSpeed(0.0, 0.0)
                viewState.showDistance("%.1f".format(0.0), challengeDistance)
            }
            .subscribe(
                {
                    distanceInMeters = it.distance / 1000

                    if (timeInSeconds != 0) {
                        viewState.showSpeed(
                            it.speed.toFloat() * 3.6,
                            it.distance.toFloat() * 3.6 / timeInSeconds
                        )
                    }
                    viewState.showDistance("%.3f".format(it.distance / 1000), challengeDistance)
                },
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
                    if (timeInSeconds != 0) timeInSeconds.toTime() else "00:00:00",
                    challengeTime
                )
                viewState.showSpeed(0.0, 0.0)

                startTime = System.currentTimeMillis()
            }
            .subscribe(
                {
                    timeInSeconds++
                    viewState.showTime(timeInSeconds.toTime(), challengeTime)

                    val h = challenge.time / 100
                    val m = challenge.time % 100

                    if (challenge.id != ChallengeController.EMPTY.id) {
                        isComplete =
                                (distanceInMeters >= challenge.distance && timeInSeconds <= m * 60 + h * 3600)
                        timeOut = timeInSeconds >= m * 60 + h * 3600
                    }
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
        traininigListener.isRun = !traininigListener.isRun

        if (traininigListener.isRun) {
            startTimer()
            viewState.run(true)
        } else {
            endTime = System.currentTimeMillis()
            viewState.run(false)
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

        if (isComplete && isItChallenge) {
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

        Completable.fromCallable { database.locationDao.clear() }
            .subscribeOn(schedulers.io())
            .subscribe({}, { Timber.e(it) })
            .connect()

        router.exit()
    }

    private fun saveTotalDistanceToDB() {
        val km = distanceInMeters / 1000

        authData.totalDistance += km

        RxFirestore.updateDocument(
            currentUserDocument,
            userTotalDistanceKey,
            authData.totalDistance
        )
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                {
                    viewState.showMessage(it.message)
                    report(it)
                    router.exit()
                }
            )
            .connect()
    }

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser!!

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val currentUserChallengesCollection
        get() = currentUserDocument
            .collection(challengesCollection)

    private val currentUserTrainingsCollection
        get() = currentUserDocument
            .collection(trainingsCollection)

    private fun saveTrainingToDB() {
        val km = distanceInMeters / 1000

        val hours = timeInSeconds / 3600

        val avgSpeed = if (hours == 0) 0.0 else km / hours.toDouble()

        val trainingInfo = Training(
            distance = km,
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
        val minutes = timeInSeconds / 60

        val km = distanceInMeters / 1000

        val hours = timeInSeconds / 3600

        val avgSpeed = if (hours == 0) 0.0 else km / hours.toDouble()

        val challengeInfo = Challenge(
            distance = km,
            hour = hours,
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
        private const val DIALOG_TAG = "rp_dialog_tag"
    }
}