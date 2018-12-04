package com.myrungo.rungo.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.myrungo.rungo.AppActivity
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.model.DBUser
import com.myrungo.rungo.model.FlowRouter
import com.myrungo.rungo.model.SchedulersProvider
import durdinapps.rxfirebase2.RxFirestore
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
            if (resultCode == RESULT_OK) {
                onOkResult()
            } else {
                onNotOkResult(IdpResponse.fromResultIntent(data))
            }
        } else {
            viewState.signIn()
        }
    }

    private fun onNotOkResult(response: IdpResponse?) {
        if (response == null) {
            //the user canceled the sign-in flow using the back button
            viewState.signIn()
            return
        }

        val error = response.error

        if (error == null) {
            viewState.signIn()
            return
        }

        Timber.e(error)

        viewState.handleSignInError(error)
    }

    private fun onOkResult() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            viewState.signIn()
        } else {
            saveToDB(user)
        }
    }

    private fun saveToDB(user: FirebaseUser) {
        val metadata = user.metadata

        val creationTimestamp = metadata?.creationTimestamp ?: System.currentTimeMillis()

        val email = user.email ?: ""
        val displayName = user.displayName ?: ""
        val phoneNumber = user.phoneNumber ?: ""
        val photoUri = user.photoUrl ?: Uri.EMPTY
        val uid = user.uid
        val isAnonymous = user.isAnonymous

        val providers = user.providers ?: emptyList()

        val provider = if (providers.isEmpty()) "" else providers[0]

        val photoUrl = photoUri.toString()

        val newUserInfo = DBUser(
            email = email,
            isAnonymous = isAnonymous,
            name = displayName,
            phoneNumber = phoneNumber,
            photoUri = photoUrl,
            provider = provider,
            reg_date = creationTimestamp,
            uid = uid,
            age = 0,
            costume = authData.currentSkin.name,
            height = 0,
            totalDistance = authData.distance
        )

        val documentReference = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(uid)

        RxFirestore.setDocument(documentReference, newUserInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                {
                    authData.name = displayName
                    authData.availableSkins = emptyList()
                    authData.completedChallenges = emptyList()

                    catController.setSkin(authData.currentSkin)

                    router.newRootFlow(Screens.MainFlow)
                },
                {
                    Timber.e(it)
                    report(it)
                    viewState.showMessage(it.message)
                }
            )
            .connect()
    }

    fun onBackPressed() = router.finishFlow()
}