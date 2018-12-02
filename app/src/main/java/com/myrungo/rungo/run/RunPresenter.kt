package com.myrungo.rungo.run

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.location.LocationRequest
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.model.ResourceManager
import com.myrungo.rungo.model.SchedulersProvider
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
    private val locationProvider: ReactiveLocationProvider,
    private val catController: CatController,
    private val schedulers: SchedulersProvider,
    private val resourceManager: ResourceManager,
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
    private var currentLocation: Location? = null
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

        locationProvider.getUpdatedLocation(req)
            .filter { isRun }
            .observeOn(schedulers.ui())
            .doOnSubscribe {  viewState.showDistance("0,0", challengeDistance) }
            .subscribe(
                { location ->
                    val temp = currentLocation
                    currentLocation = location

                    val distance = if (temp != null) currentLocation?.distanceTo(temp) ?: 0f else 0f
                    lastDistance = distance
                    lastTime = initTime
                    currentDistance += if (distance > 0) distance/1000 else 0f
                    viewState.showDistance("%.1f".format(currentDistance), challengeDistance)

                    authData.distance = authData.distance + currentDistance
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
        isRun = !isRun
        viewState.run(isRun)
        if (isRun) {
            startTimer()
        } else {
            timerDisposable?.dispose()
        }
    }

    fun onStopClicked() {
        if (!isComplete && challenge.id != ChallengeController.EMPTY.id) {
            viewState.showDialog(
                title = "Нет больше сил?",
                msg = "Твой вызов еще не выполнен!\nЗакончить тренировку?",
                tag = DIALOG_TAG
            )
        } else if (!isComplete) {
            viewState.showDialog(
                title = "Закончить тренировку?",
                msg = "",
                tag = DIALOG_TAG
            )
        }
    }

    fun exit() = router.exit()

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
    }

    companion object {
        private const val LOCATION_TIMEOUT = 10000L
        private const val LOCATION_UPDATE_INTERVAL = 100L
        private const val DIALOG_TAG = "rp_dialog_tag"
    }
}