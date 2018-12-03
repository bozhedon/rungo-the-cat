package com.myrungo.rungo.auth

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.myrungo.rungo.AppActivity
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.model.FlowRouter
import com.myrungo.rungo.model.SchedulersProvider
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class AuthPresenter @Inject constructor(
    private val schedulers: SchedulersProvider,
    private val router: FlowRouter,
    private val authData: AuthHolder,
    private val catController: CatController
) : BasePresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showButton(false)
        viewState.signIn()
    }

    fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppActivity.RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)!!

                task.getResult(ApiException::class.java)
                    ?.let { account ->
                        Completable
                            .fromAction {
                                authData.name = account.displayName ?: "User"
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
                                {
                                    firebaseAuthWithGoogle(account)
                                        .subscribe(
                                            {
                                                router.newRootFlow(Screens.MainFlow)
                                            },
                                            {
                                                Timber.e(it)
                                            })
                                },
                                {
                                    Timber.e(it)
                                }
                            )
                            .connect()
                    }
            } catch (apiException: ApiException) {
                Timber.e(apiException)
                viewState.showButton(true)
            }
        } else {
            viewState.showButton(true)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Maybe<AuthResult> {
        Timber.d("firebaseAuthWithGoogle: ${account.id!!}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        return RxFirebaseAuth.signInWithCredential(FirebaseAuth.getInstance(), credential)
    }

    fun onBackPressed() = router.finishFlow()
}