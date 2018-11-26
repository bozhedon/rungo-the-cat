package com.myrungo.rungo.customize_done

import com.arellomobile.mvp.MvpView
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.model.MainNavigationController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class CustomizeDonePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController
): BasePresenter<MvpView>() {

    fun onBackPressed(goHome: Boolean) {
        if (goHome) {
            navigation.open(0)
        }
        router.exit()
    }
}