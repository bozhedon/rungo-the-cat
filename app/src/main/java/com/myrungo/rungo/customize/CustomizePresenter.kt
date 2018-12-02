package com.myrungo.rungo.customize

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Screens
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.model.MainNavigationController
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class CustomizePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController,
    private val authData: AuthHolder,
    private val catController: CatController
) : BasePresenter<CustomizeView>() {

    private var skinResId: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        catController.skinState
            .subscribe(
                { handleSkin(it) },
                { Timber.e(it) }
            )
            .connect()

        viewState.showSkins(
            listOf(
                SkinItem(0, R.drawable.normal_cat_sportsuniform, true),
                SkinItem(1, R.drawable.bad_cat_jacket, false),
                SkinItem(2, R.drawable.bussiness_cat_cloth, false),
                SkinItem(3, R.drawable.karate_cat_kimono, false)
            )
        )
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

    fun onSkinClicked(skin: SkinItem) {
        if (!skin.isAvailable) return

        val skinRes = getSkinReference(skin.id)

        viewState.showSkinReference(skinRes)
        skinResId = skinRes
    }

    fun onBackPressed() = navigation.open(0)

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