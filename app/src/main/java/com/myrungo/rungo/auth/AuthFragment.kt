package com.myrungo.rungo.auth

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes.*
import com.firebase.ui.auth.FirebaseUiException
import com.myrungo.rungo.AppActivity.Companion.RC_SIGN_IN
import com.myrungo.rungo.BaseFragment
import com.myrungo.rungo.R
import com.myrungo.rungo.Scopes
import com.myrungo.rungo.visible
import kotlinx.android.synthetic.main.fragment_auth.*
import toothpick.Toothpick

class AuthFragment : BaseFragment(), AuthView {
    override val layoutRes = R.layout.fragment_auth

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    @ProvidePresenter
    fun providePresenter() = Toothpick
        .openScope(Scopes.AUTH)
        .getInstance(AuthPresenter::class.java)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth_button.setOnClickListener { signIn() }
    }

    override fun signIn() {
        val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false, true)
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_logo)
            .setTheme(R.style.AppTheme)
            .build()

        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun handleSignInError(error: FirebaseUiException) {
        val errorCode = error.errorCode

        val message = when (errorCode) {
            NO_NETWORK -> context!!.getString(R.string.no_internet_connection)
            PLAY_SERVICES_UPDATE_CANCELLED -> context!!.getString(R.string.play_services_update_cancelled)
            DEVELOPER_ERROR -> context!!.getString(R.string.developer_error)
            PROVIDER_ERROR -> context!!.getString(R.string.provider_error)
            ANONYMOUS_UPGRADE_MERGE_CONFLICT -> context!!.getString(R.string.user_account_merge_conflict)
            EMAIL_MISMATCH_ERROR -> context!!.getString(R.string.you_are_attempting_to_sign_in_a_different_email_than_previously_provided)
            UNKNOWN_ERROR -> context!!.getString(R.string.unknown_error_has_occured)
            else -> context!!.getString(R.string.unknown_error_has_occured)
        }

        reportError(error)

        showMessage(message)
    }

    override fun showMessage(message: String?) {
        if (message != null) {
            val activity = activity

            if (activity != null) {
                Snackbar.make(
                    activity.findViewById<View>(android.R.id.content),
                    message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun showButton(show: Boolean) {
        auth_button.visible(show)
    }

    override fun showProgress(show: Boolean) {
        showProgressDialog(show)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.handleAuthResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}