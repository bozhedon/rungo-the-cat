package com.myrungo.rungo.model

/**
 * In firestore fields will be named like parameter's fields
 * For example: if in DB field == "reg_date", but model's field == "regDate"
 * it will be renamed to "regDate"
 */
data class DBUser(

    val email: String = "",

    val isAnonymous: Boolean = false,

    var name: String = "",

    var phoneNumber: String = "",

    var photoUri: String = "",

    var provider: String = "",

    //must be EXACTLY with underscore, because of firebase authentication
    var reg_date: Long = 0,

    /**
     * Primary key
     */
    var uid: String,

    var age: Int = 0,

    var costume: String = "",

    var height: Int = 0,

    var totalDistance: Double = 0.0

)