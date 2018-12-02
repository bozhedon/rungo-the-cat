package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.model.ResourceManager
import com.myrungo.rungo.model.SchedulersProvider
import com.myrungo.rungo.toTime
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
    private val schedulers: SchedulersProvider,
    private val resourceManager: ResourceManager
) : BasePresenter<RunView>() {
    private var currentTab = 0
    private var isRun = false
    private var timerDisposable: Disposable? = null
    private var initTime = 0
    private var isComplete = false

    private val challengeTime =
            if (challenge.id != ChallengeController.EMPTY.id) "${challenge.time/100}:${challenge.time%100}"
            else ""

    private val challengeDistance =
        if (challenge.id != ChallengeController.EMPTY.id) resourceManager.getString(R.string.distance, challenge.distance)
        else ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showDistance("0 км", challengeDistance)

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
                    if (initTime != 0) initTime.toTime() else "0:00",
                    challengeTime
                )
            }
            .subscribe(
                {
                    initTime++
                    viewState.showTime(initTime.toTime(), challengeTime)
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
                msg = "Твой вызов еще не выоплнен!\nЗакончить тренировку?",
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
        private const val DIALOG_TAG = "rp_dialog_tag"
    }
}