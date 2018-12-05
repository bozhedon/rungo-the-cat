package com.myrungo.rungo.main.choice

import android.Manifest
import android.support.v4.app.Fragment
import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.model.MainNavigationController
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChoicePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController
) : BasePresenter<ChoiceView>() {

    fun onChallengeClicked() {
        viewState.dismiss()
        navigation.open(3)
    }

    fun onTrainingClicked(fragment: Fragment) {
        RxPermissions(fragment)
            .requestEachCombined(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .subscribe(
                { onSuccessRequestLocationPermissions(it) },
                { Timber.e(it) }
            )
            .connect()
    }

    fun onBackPressed() = viewState.dismiss()

    private fun onSuccessRequestLocationPermissions(permission: Permission) {
        when {
            permission.granted -> {
                viewState.dismiss()
                router.navigateTo(Screens.RunFlow())
            }
            permission.shouldShowRequestPermissionRationale -> {
                //user denied permission, but dont tap "dont ask"
                viewState.showNeedLocationPermissionRationaleDialog()
            }
            else -> {
                //user denied permission with tap on "dont ask"
                viewState.showGoSettingsDialog()
            }
        }
    }

    fun onGoSettingsForGetCurrentLocationPositiveClick() {
        viewState.startSettingsActivityForResult()
    }

}