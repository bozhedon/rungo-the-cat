package com.myrungo.rungo.customize

import com.arellomobile.mvp.InjectViewState
import com.myrungo.rungo.BasePresenter
import com.myrungo.rungo.R
import com.myrungo.rungo.Screens
import com.myrungo.rungo.model.MainNavigationController
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class CustomizePresenter @Inject constructor(
    private val router: Router,
    private val navigation: MainNavigationController
) : BasePresenter<CustomizeView>() {

    private var skinResId: Int? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showSkins(
            listOf(
                SkinItem(0, R.drawable.normal_cat_sportsuniform, true),
                SkinItem(1, R.drawable.bad_cat_jacket, false),
                SkinItem(2, R.drawable.bussiness_cat_cloth, false),
                SkinItem(3, R.drawable.karate_cat_kimono, false)
            )
        )
    }

    fun onSelectClicked() {
        skinResId?.let { router.navigateTo(Screens.CustomizeDone(it)) }
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
        else -> 0
    }
}