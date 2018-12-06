package com.myrungo.rungo.auth

import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeItem

interface AuthHolder {
    var name: String
    var totalDistanceInKm: Double

    var weekDistance: Double
    var monthDistance: Double
    var yearDistance: Double

    var availableSkins: List<CatView.Skins>
    var currentSkin: CatView.Skins
    var completedChallenges: List<ChallengeItem>
}