package com.myrungo.rungo.home

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class HomePresenter @Inject constructor(
    private val router: Router,
    private val catController: CatController
): BasePresenter<HomeView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        catController.skinState
            .subscribe(
                { handleSkin(it) },
                { Timber.e(it) }
            )
            .connect()

        viewState.greet()
    }

    private fun handleSkin(skin: CatView.Skins) {
        viewState.showCat(skin)
    }

    fun onBackPressed() = router.exit()
}