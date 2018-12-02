package com.myrungo.rungo.challenge

import com.jakewharton.rxrelay2.PublishRelay
import com.myrungo.rungo.cat.CatView
import io.reactivex.Observable

class ChallengeController {
    companion object {
        val EMPTY = ChallengeItem(-1, 0, 0, 0)
        fun getAward(challenge: ChallengeItem) = when (challenge.id) {
            0 -> CatView.Skins.BAD
            1 -> CatView.Skins.BUSINESS
            2 -> CatView.Skins.COMMON
            else -> null
        }
    }
    private val relay: PublishRelay<ChallengeItem> = PublishRelay.create()

    val state: Observable<ChallengeItem> = relay
    fun finishChallenge(challenge: ChallengeItem) = relay.accept(challenge)
}