package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.model.database.AppDatabase
import com.myrungo.rungo.model.location.TraininigListener
import com.myrungo.rungo.toTime
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
    private var initTime = 0
    private var isComplete = false
    private var timeOut = false
    private var currentDistance = 0f
    private var lastDistance = 0f
    private var lastTime = 0

    private val challengeTime =
            if (challenge.id != ChallengeController.EMPTY.id) "${challenge.time/100}:${challenge.time%100}"
            else ""

    private val challengeDistance =
        if (challenge.id != ChallengeController.EMPTY.id) challenge.distance.toString()
        else ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        database.locationDao.listenLastLocation()
            .observeOn(schedulers.ui())
            .subscribe(
                { location ->
                    //TODO отображать метки
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
                viewState.showSpeed(0f, 0f)
            }
            .subscribe(
                {
                    initTime++
                    viewState.showTime(initTime.toTime(), challengeTime)

                    val h = challenge.time / 100
                    val m = challenge.time % 100

                    if (challenge.id != ChallengeController.EMPTY.id) {
                        isComplete = (currentDistance >= challenge.distance && initTime <= m * 60 + h * 3600)
                        timeOut = initTime >= m * 60 + h * 3600
                    }

                    viewState.showSpeed(
                        if ((initTime - lastTime).toFloat()/3600 > 0) (lastDistance/1000)/((initTime - lastTime).toFloat()/3600) else 0f,
                        if (initTime.toFloat()/3600 > 0) currentDistance/(initTime.toFloat()/3600) else 0f
                    )
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
        if (isComplete && challenge.id != ChallengeController.EMPTY.id) {
            challengeController.finishChallenge(challenge)

            val availableSkins = mutableListOf<CatView.Skins>()
            availableSkins.addAll(authData.availableSkins)
            ChallengeController.getAward(challenge)?.let { availableSkins.add(it) }

            authData.availableSkins = availableSkins

            navigationController.open(1)
        }
        router.exit()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
    }

    companion object {
        private const val DIALOG_TAG = "rp_dialog_tag"
    }
}