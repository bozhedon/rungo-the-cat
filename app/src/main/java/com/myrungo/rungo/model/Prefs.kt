package com.myrungo.rungo.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeItem
import javax.inject.Inject

class Prefs @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : AuthHolder {
    private val AUTH_DATA = "auth_data"
    private val KEY_NAME = "ad_name"
    private val KEY_DISTANCE = "ad_distance"
    private val KEY_DISTANCE_WEEK = "ad_distance_week"
    private val KEY_DISTANCE_MONTH = "ad_distance_month"
    private val KEY_DISTANCE_YEAR = "ad_distance_year"
    private val KEY_SKINS = "ad_skins"
    private val KEY_CURRENT_SKIN = "ad_current_skin"
    private val KEY_CHALLENGES = "ad_challenges"

    private fun getSharedPreferences(prefsName: String) = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override var name: String
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_NAME, null) ?: "User"
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_NAME, value).apply()
        }

    override var distance: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE, "0")?.toDoubleOrNull() ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_DISTANCE, value.toString()).apply()
        }

    override var distanceWeek: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_WEEK, "0")?.toDoubleOrNull() ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_DISTANCE_WEEK, value.toString()).apply()
        }

    override var distanceMonth: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_MONTH, "0")?.toDoubleOrNull() ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_DISTANCE_MONTH, value.toString()).apply()
        }

    override var distanceYear: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_YEAR, "0")?.toDoubleOrNull() ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_DISTANCE_YEAR, value.toString()).apply()
        }

    override var availableSkins: List<CatView.Skins>
        get() {
            try {
                val json = getSharedPreferences(AUTH_DATA).getString(KEY_SKINS, null)
                val jsonArray = gson.fromJson(json, JsonElement::class.java).asJsonArray
                return jsonArray.map { gson.fromJson(it, CatView.Skins::class.java) }
            } catch (e: Exception) {
                return emptyList()
            }
        }
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_SKINS, gson.toJson(value)).apply()
        }

    override var currentSkin: CatView.Skins
        get() {
            try {
                val json = getSharedPreferences(AUTH_DATA).getString(KEY_CURRENT_SKIN, null)
                return gson.fromJson(json, CatView.Skins::class.java)
            } catch (e: Exception) {
                return CatView.Skins.COMMON
            }
        }
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_CURRENT_SKIN, gson.toJson(value)).apply()
        }

    override var completedChallenges: List<ChallengeItem>
        get() {
            try {
                val json = getSharedPreferences(AUTH_DATA).getString(KEY_CHALLENGES, null)
                val jsonArray = gson.fromJson(json, JsonElement::class.java).asJsonArray
                return jsonArray.map { gson.fromJson(it, ChallengeItem::class.java) }
            } catch (e: Exception) {
                return emptyList()
            }
        }
        set(value) {
            getSharedPreferences(AUTH_DATA).edit().putString(KEY_CHALLENGES, gson.toJson(value)).apply()
        }
}