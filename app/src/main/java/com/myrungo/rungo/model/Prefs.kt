package com.myrungo.rungo.model

import android.annotation.SuppressLint
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.constants.*
import com.yandex.metrica.YandexMetrica
import durdinapps.rxfirebase2.RxFirestore
import timber.log.Timber
import javax.inject.Inject

class Prefs @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val schedulers: SchedulersProvider
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

    private fun getSharedPreferences(prefsName: String) =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override var name: String
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_NAME, null) ?: "User"
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_NAME, value)
                .apply()
        }

    override var distance: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE, "0")?.toDoubleOrNull()
            ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE, value.toString())
                .apply()
        }

    override var distanceWeek: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_WEEK, "0")?.toDoubleOrNull()
            ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_WEEK, value.toString())
                .apply()
        }

    override var distanceMonth: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_MONTH, "0")?.toDoubleOrNull()
            ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_MONTH, value.toString())
                .apply()
        }

    override var distanceYear: Double
        get() = getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_YEAR, "0")?.toDoubleOrNull()
            ?: 0.toDouble()
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_YEAR, value.toString())
                .apply()
        }

    override var availableSkins: List<CatView.Skins>
        get() = try {
            val json = getSharedPreferences(AUTH_DATA).getString(KEY_SKINS, null)
            val jsonArray = gson.fromJson(json, JsonElement::class.java).asJsonArray
            val availableSkins = jsonArray.map { gson.fromJson(it, CatView.Skins::class.java) }

            checkSkinsMatch(availableSkins)

            availableSkins
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_SKINS, gson.toJson(value))
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkSkinsMatch(availableSkins: List<CatView.Skins>) {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            val collectionReference = FirebaseFirestore.getInstance()
                .collection(usersCollection)
                .document(currentUser.uid)
                .collection(challengesCollection)

            RxFirestore.getCollection(collectionReference)
                .subscribeOn(schedulers.io())
                .subscribeOn(schedulers.ui())
                .subscribe(
                    { onGetChallengesCollection(it, availableSkins) },
                    {
                        Timber.e(it)
                        reportError(it)
                    }
                )
        }
    }

    private fun onGetChallengesCollection(
        snapshot: QuerySnapshot,
        availableSkins: List<CatView.Skins>
    ) {
        val dbSkins = mutableSetOf<String>()

        for (document in snapshot.documents) {
            document.data?.let { data ->
                data[challengeRewardKey]?.let { reward ->
                    dbSkins += reward.toString()
                }
            }
        }

        val spSkins = availableSkins.map { it.name }.toSet()

        val skinsThatDbDoesNotContain = spSkins.minus(dbSkins)

        for (skinName in skinsThatDbDoesNotContain) {
            //shared preferences contains skin, that does not exist in DB
            availableSkins.find { it.name == skinName }?.let { skinForSave ->
                saveToDBThisSkin(skinForSave)
            }
        }
    }

    private fun saveToDBThisSkin(skinForSave: CatView.Skins) {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            val challengeInfo = mutableMapOf<String, Any>()

            challengeInfo[challengeDistanceKey] = 0
            challengeInfo[challengeHourKey] = 0
            challengeInfo[challengeIdKey] = 0
            challengeInfo[challengeImgURLKey] = ""
            challengeInfo[challengeIsCompleteKey] = true
            challengeInfo[challengeMinutesKey] = 0
            challengeInfo[challengeRewardKey] = skinForSave.name

            val collectionReference = FirebaseFirestore.getInstance()
                .collection(usersCollection)
                .document(currentUser.uid)
                .collection(challengesCollection)

            RxFirestore.addDocument(collectionReference, challengeInfo)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe(
                    { },
                    {
                        Timber.e(it)
                        reportError(it)
                    }
                )
        }
    }

    override var currentSkin: CatView.Skins
        get() = try {
            val json = getSharedPreferences(AUTH_DATA).getString(KEY_CURRENT_SKIN, null)
            gson.fromJson(json, CatView.Skins::class.java)
        } catch (e: Exception) {
            CatView.Skins.COMMON
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_CURRENT_SKIN, gson.toJson(value))
                .apply()
        }

    override var completedChallenges: List<ChallengeItem>
        get() = try {
            val json = getSharedPreferences(AUTH_DATA).getString(KEY_CHALLENGES, null)
            val jsonArray = gson.fromJson(json, JsonElement::class.java).asJsonArray
            jsonArray.map { gson.fromJson(it, ChallengeItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_CHALLENGES, gson.toJson(value))
                .apply()
        }

    private fun reportError(throwable: Throwable) {
        Crashlytics.logException(throwable)
        YandexMetrica.reportUnhandledException(throwable)
    }
}