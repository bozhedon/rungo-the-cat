package com.myrungo.rungo.challenge.accept

import android.Manifest
import android.support.v4.app.Fragment
import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.challenge.ChallengeItem
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChallengeAcceptPresenter @Inject constructor(
    private val router: Router
) : BasePresenter<ChallengeAcceptView>() {

    fun accept(challenge: ChallengeItem?) {
        router.navigateTo(Screens.RunFlow(challenge))
    }

    fun onChallengeAcceptClicked(fragment: Fragment) {
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

    private fun onSuccessRequestLocationPermissions(permission: Permission) {
        when {
            permission.granted -> {
                viewState.onPermissionGranted()
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