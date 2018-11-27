package com.myrungo.rungo.auth

import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
        .getInstance(AuthPresenter::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth_button.setOnClickListener { signIn() }
    }

    override fun signIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        startActivityForResult(GoogleSignIn.getClient(requireContext(), options).signInIntent, RC_SIGN_IN)
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