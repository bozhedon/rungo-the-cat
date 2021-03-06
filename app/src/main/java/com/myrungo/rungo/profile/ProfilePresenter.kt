package com.myrungo.rungo.profile

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Screens
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.utils.NetworkManager
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController,
    private val catController: CatController,
    private val authData: AuthHolder,
    private val networkManager: NetworkManager
) : BasePresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (!networkManager.isConnectedToInternet) {
            viewState.showMessage("Необходимо интернет соединение")
            router.navigateTo(Screens.MainFlow)
            return
        }

        viewState.showTab(0)
    }

    fun showInfo() {
        catController.skinState
            .subscribe(
                { handleSkin(it) },
                { report(it) }
            )
            .connect()

        viewState.showDetails(authData.name, authData.totalDistanceInKm)
    }

    fun onTabClicked(position: Int) {
        viewState.showTab(position)
    }

    fun onBackPressed() = navigation.open(0)

    private fun handleSkin(skin: CatView.Skins) {
        viewState.showCat(
            when (skin) {
                CatView.Skins.COMMON -> R.drawable.common_cat
                CatView.Skins.BAD -> R.drawable.bad_cat
                CatView.Skins.BUSINESS -> R.drawable.bussiness_cat
                CatView.Skins.KARATE -> R.drawable.karate_cat
                CatView.Skins.NORMAL -> R.drawable.normal_cat
            }
        )
    }
}