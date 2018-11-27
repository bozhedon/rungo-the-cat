package com.myrungo.rungo.run

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.cat.CatController
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class RunPresenter @Inject constructor(
    private val router: Router,
    private val catController: CatController
) : BasePresenter<RunView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        catController.skinState
            .subscribe(
                { viewState.showSkin(it) },
                { Timber.e(it) }
            )
            .connect()
    }

    fun onBackPressed() {
        viewState.showDialog()
    }
}