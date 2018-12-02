package com.myrungo.rungo.challenge

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class ChallengeController {
    companion object {
        val EMPTY = ChallengeItem(-1, 0, 0, 0)
    }
    private val relay: BehaviorRelay<ChallengeItem> = BehaviorRelay.create()

    val state: Observable<ChallengeItem> = relay
    fun setChallenge(challenge: ChallengeItem) = relay.accept(challenge)
    fun clear() = relay.accept(EMPTY)
}