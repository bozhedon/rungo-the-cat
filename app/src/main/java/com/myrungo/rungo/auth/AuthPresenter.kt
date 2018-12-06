package com.myrungo.rungo.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.myrungo.rungo.AppActivity
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.Screens
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.model.DBUser
import com.myrungo.rungo.model.FlowRouter
import com.myrungo.rungo.model.SchedulersProvider
import durdinapps.rxfirebase2.RxFirestore
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
        val userDocument = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(user.uid)

        updateUserInfoInDBOrCreateNew(
            userDocument,
            createNewUserInfoForUpdate(user),
            createNewUserInfoForSet(user),
            user.displayName ?: ""
        )
    }

    private fun updateUserInfoInDBOrCreateNew(
        userDocument: DocumentReference,
        map: Map<String, Any>,
        newUserInfo: DBUser,
        displayName: String
    ) {
        RxFirestore.updateDocument(userDocument, map)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                {
                    onSuccessAuthorization(displayName)
                },
                {
                    if (it is FirebaseFirestoreException && it.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        //DB has no current user info, so it must be created
                        createNewUserInfoForSet(userDocument, newUserInfo, displayName)
                    } else {
                        report(it)
                        viewState.showMessage(it.message)
                    }
                }
            )
            .connect()
    }

    private fun createNewUserInfoForSet(
        userDocument: DocumentReference,
        newUserInfo: DBUser,
        displayName: String
    ) {
        RxFirestore.setDocument(userDocument, newUserInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { onSuccessAuthorization(displayName) },
                {
                    report(it)
                    viewState.showMessage(it.message)
                }
            )
            .connect()
    }

    private fun onSuccessAuthorization(displayName: String) {
        authData.name = displayName
        authData.availableSkins = emptyList()
        authData.completedChallenges = emptyList()

        catController.setSkin(authData.currentSkin)

        router.newRootFlow(Screens.MainFlow)
    }

    private fun createNewUserInfoForUpdate(user: FirebaseUser): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        val email = user.email ?: ""
        val displayName = user.displayName ?: ""
        val phoneNumber = user.phoneNumber ?: ""

        val uid = user.uid
        val isAnonymous = user.isAnonymous

        val provider = run {
            val providers = user.providers ?: emptyList()

            if (providers.isEmpty()) "" else providers[0] ?: ""
        }

        val photoUrl = run {
            val photoUri = user.photoUrl ?: Uri.EMPTY

            photoUri.toString()
        }

        map["email"] = email
        map["isAnonymous"] = isAnonymous
        map["photoUri"] = photoUrl
        map["provider"] = provider
        map["uid"] = uid
        map["phoneNumber"] = phoneNumber
        map["name"] = displayName

        return map
    }

    private fun createNewUserInfoForSet(user: FirebaseUser): DBUser {
        val creationTimestamp = run {
            val metadata = user.metadata

            metadata?.creationTimestamp ?: System.currentTimeMillis()
        }

        val email = user.email ?: ""
        val displayName = user.displayName ?: ""
        val phoneNumber = user.phoneNumber ?: ""

        val uid = user.uid
        val isAnonymous = user.isAnonymous

        val provider = run {
            val providers = user.providers ?: emptyList()

            if (providers.isEmpty()) "" else providers[0] ?: ""
        }

        val photoUrl = run {
            val photoUri = user.photoUrl ?: Uri.EMPTY

            photoUri.toString()
        }

        return DBUser(
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
            totalDistance = authData.totalDistanceInKm,
            monthDistance = authData.monthDistance,
            yearDistance = authData.yearDistance
        )
    }

    fun onBackPressed() = router.finishFlow()
}