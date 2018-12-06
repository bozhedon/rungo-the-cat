package com.myrungo.rungo.model

import com.google.firebase.firestore.PropertyName
import com.myrungo.rungo.utils.constants.*

data class DBUser(

    @get:PropertyName(userEmailKey)
    @PropertyName(userEmailKey)
    val email: String = "",

    @get:PropertyName(userIsAnonymousKey)
    @PropertyName(userIsAnonymousKey)
    val isAnonymous: Boolean = false,

    @get:PropertyName(userNameKey)
    @PropertyName(userNameKey)
    var name: String = "",

    @get:PropertyName(userPhoneNumberKey)
    @PropertyName(userPhoneNumberKey)
    var phoneNumber: String = "",

    @get:PropertyName(userPhotoUriKey)
    @PropertyName(userPhotoUriKey)
    var photoUri: String = "",

    @get:PropertyName(userProviderKey)
    @PropertyName(userProviderKey)
    var provider: String = "",

    @get:PropertyName(userRegDateKey)
    @PropertyName(userRegDateKey)
    var regDate: Long = 0,

    /**
     * Primary key
     */
    @get:PropertyName(userUidKey)
    @PropertyName(userUidKey)
    var uid: String = "",

    @get:PropertyName(userAgeKey)
    @PropertyName(userAgeKey)
    var age: Int = 0,

    @get:PropertyName(userCostumeKey)
    @PropertyName(userCostumeKey)
    var costume: String = "",

    @get:PropertyName(userHeightKey)
    @PropertyName(userHeightKey)
    var height: Int = 0,

    @get:PropertyName(userTotalDistanceKey)
    @PropertyName(userTotalDistanceKey)
    var totalDistance: Double = 0.0,

    @get:PropertyName(userWeekDistanceKey)
    @PropertyName(userWeekDistanceKey)
    val weekDistance: Double = 0.0,

    @get:PropertyName(userMonthDistanceKey)
    @PropertyName(userMonthDistanceKey)
    val monthDistance: Double = 0.0,

    @get:PropertyName(userYearDistanceKey)
    @PropertyName(userYearDistanceKey)
    val yearDistance: Double = 0.0

)