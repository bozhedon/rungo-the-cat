package com.myrungo.rungo.customize

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Screens
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeController
import com.myrungo.rungo.model.MainNavigationController
import com.myrungo.rungo.model.SchedulersProvider
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class CustomizePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController,
    private val authData: AuthHolder,
    private val catController: CatController,
    private val challengeController: ChallengeController,
    private val schedulers: SchedulersProvider
) : BasePresenter<CustomizeView>() {

    private var skinResId: Int? = null

    fun onStart() {
        showAvailableSkins()
    }

    fun onSelectClicked() {
        if (authData.currentSkin == getSkin(skinResId ?: R.drawable.common_cat)) return

        skinResId?.let {
            catController.setSkin(getSkin(it))
            router.navigateTo(Screens.CustomizeDone(it))
        }
    }

    fun onDefaultSelected() {
        skinResId = null
        catController.setSkin(CatView.Skins.COMMON)
    }

    fun onSkinClicked(skinItem: SkinItem) {
        if (!skinItem.isAvailable) return

        val skinRes = getSkinReference(skinItem.id)

        viewState.showSkinReference(skinRes)

        skinResId = skinRes
    }

    fun onBackPressed() = navigation.open(0)

    private fun showAvailableSkins() {
        catController.skinState
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { handleSkin(it) },
                { report(it) }
            )
            .connect()

        challengeController.state
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                {
                    val skins = listOf(
                        SkinItem(
                            0,
                            R.drawable.normal_cat_sportsuniform,
                            true
                        ),
                        SkinItem(
                            1,
                            R.drawable.bad_cat_jacket,
                            it.awardRes == R.drawable.bad_cat_jacket
                        ),
                        SkinItem(
                            2,
                            R.drawable.bussiness_cat_cloth,
                            it.awardRes == R.drawable.bussiness_cat_cloth
                        ),
                        SkinItem(
                            3,
                            R.drawable.karate_cat_kimono,
                            it.awardRes == R.drawable.karate_cat_kimono
                        )
                    )

                    viewState.showSkins(skins)
                },
                { report(it) }
            )
            .connect()

        val skins = listOf(
            SkinItem(0, R.drawable.normal_cat_sportsuniform, true),
            SkinItem(
                1,
                R.drawable.bad_cat_jacket,
                authData.availableSkins.find { it == CatView.Skins.BAD } != null
            ),
            SkinItem(
                2,
                R.drawable.bussiness_cat_cloth,
                authData.availableSkins.find { it == CatView.Skins.BUSINESS } != null
            ),
            SkinItem(
                3,
                R.drawable.karate_cat_kimono,
                authData.availableSkins.find { it == CatView.Skins.KARATE } != null
            )
        )

        viewState.showSkins(skins)
    }

    private fun handleSkin(skin: CatView.Skins) {
        viewState.showSkinReference(
            when (skin) {
                CatView.Skins.COMMON -> R.drawable.common_cat
                CatView.Skins.BAD -> R.drawable.bad_cat
                CatView.Skins.BUSINESS -> R.drawable.bussiness_cat
                CatView.Skins.KARATE -> R.drawable.karate_cat
                CatView.Skins.NORMAL -> R.drawable.normal_cat
            }
        )

        authData.currentSkin = skin
    }

    private fun getSkinReference(id: Int) = when (id) {
        0 -> R.drawable.normal_cat
        1 -> R.drawable.bad_cat
        2 -> R.drawable.bussiness_cat
        3 -> R.drawable.karate_cat
        else -> R.drawable.common_cat
    }

    private fun getSkin(resId: Int) = when (resId) {
        R.drawable.common_cat -> CatView.Skins.COMMON
        R.drawable.normal_cat -> CatView.Skins.NORMAL
        R.drawable.bad_cat -> CatView.Skins.BAD
        R.drawable.bussiness_cat -> CatView.Skins.BUSINESS
        R.drawable.karate_cat -> CatView.Skins.KARATE
        else -> CatView.Skins.COMMON
    }

}