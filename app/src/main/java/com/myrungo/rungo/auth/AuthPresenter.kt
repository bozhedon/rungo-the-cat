package com.myrungo.rungo.auth

import android.app.Activity
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.myrungo.rungo.AppActivity
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.model.FlowRouter
import com.myrungo.rungo.model.SchedulersProvider
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class AuthPresenter @Inject constructor(
    private val schedulers: SchedulersProvider,
    private val router: FlowRouter,
    private val authData: AuthHolder,
    private val catController: CatController
): BasePresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showButton(false)
        viewState.signIn()
    }

    fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppActivity.RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)?.let {
                    Completable.fromAction {
                        authData.name = it.displayName ?: "User"
                        //TODO
                        authData.availableSkins = emptyList()
                        authData.completedChallenges = emptyList()

                        catController.setSkin(authData.currentSkin)
                    }
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.ui())
                        .doOnSubscribe { viewState.showProgress(true) }
                        .doAfterTerminate { viewState.showProgress(false) }
                        .subscribe(
                            { router.newRootFlow(Screens.MainFlow) },
                            { Timber.e(it) }
                        )
                        .connect()
                }
            } catch (e: ApiException) {
                Timber.e(e)
            }
        } else {
            viewState.showButton(true)
        }
    }

    fun onBackPressed() = router.finishFlow()
}