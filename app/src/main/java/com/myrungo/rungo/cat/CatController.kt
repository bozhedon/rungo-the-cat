package com.myrungo.rungo.cat

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class CatController {
    private val skinRelay: BehaviorRelay<CatView.Skins> = BehaviorRelay.create()

    val skinState: Observable<CatView.Skins> = skinRelay
    fun setSkin(skin: CatView.Skins) = skinRelay.accept(skin)
}