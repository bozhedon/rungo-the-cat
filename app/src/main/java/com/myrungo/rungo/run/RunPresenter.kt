package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.model.PrimitiveWrapper
import com.myrungo.rungo.model.qualifier.ArgTraining
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class RunPresenter @Inject constructor(
    @ArgTraining val isTraining: PrimitiveWrapper<Boolean>,
    private val router: Router,
    private val catController: CatController
) : BasePresenter<RunView>() {
    private var currentTab = 0
    private var isRun = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        catController.skinState
            .subscribe(
                { viewState.showSkin(it) },
                { Timber.e(it) }
            )
            .connect()

        viewState.run(isRun)
    }

    fun onTabClicked(position: Int) {
        if (position == currentTab) return

        viewState.showMap(position == 1)
        currentTab = position
    }

    fun onStartClicked() {
        isRun = !isRun
        viewState.run(isRun)
    }

    fun onStopClicked() {

    }

    fun onBackPressed() {
        viewState.showDialog()
    }
}