package com.myrungo.rungo.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.myrungo.rungo.R
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatView
import com.myrungo.rungo.challenge.ChallengeItem
import com.myrungo.rungo.constants.challengesCollection
import com.myrungo.rungo.constants.usersCollection
import com.myrungo.rungo.profile.stats.models.Challenge
import com.yandex.metrica.YandexMetrica
import durdinapps.rxfirebase2.RxFirestore
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

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
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)!!

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser
            ?: throw RuntimeException("User must sign in app")

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    private val authData
        get() = getSharedPreferences(AUTH_DATA)

    override var name: String
        get() {
            val nameFromSP = authData.getString(KEY_NAME, "User")!!

            asyncLoadNameFromDBAndSave()

            return nameFromSP
        }
        set(value) {
            saveNameToSP(value)
        }

    private fun saveNameToSP(nameValue: String) {
        authData.edit {
            putString(KEY_NAME, nameValue)
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadNameFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForNameChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForNameChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            saveNameToSP(currentUserInfoFromDB.name)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var totalDistanceInKm: Double
        get() {
            val totalDistanceInKmFromSP = authData.getString(KEY_DISTANCE, "0")!!.toDouble()

            asyncLoadTotalDistanceFromDBAndSave()

            return totalDistanceInKmFromSP
        }
        set(value) {
            saveTotalDistanceToSP(value)
        }

    private fun saveTotalDistanceToSP(value: Double) {
        authData.edit {
            putString(KEY_DISTANCE, value.toString())
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadTotalDistanceFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForTotalDistanceChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForTotalDistanceChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            saveTotalDistanceToSP(currentUserInfoFromDB.totalDistance)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var weekDistance: Double
        get() {
            val weekDistanceFromSP = authData.getString(KEY_DISTANCE_WEEK, "0")!!.toDouble()

            asyncLoadWeekDistanceFromDBAndSave()

            return weekDistanceFromSP
        }
        set(value) {
            saveWeekDistanceToSP(value)
        }

    private fun saveWeekDistanceToSP(value: Double) {
        authData.edit {
            putString(KEY_DISTANCE_WEEK, value.toString())
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadWeekDistanceFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForWeekDistanceChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForWeekDistanceChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            saveWeekDistanceToSP(currentUserInfoFromDB.weekDistance)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var monthDistance: Double
        get() {
            val monthDistanceFromSP = authData.getString(KEY_DISTANCE_MONTH, "0")!!.toDouble()

            asyncLoadMonthDistanceFromDBAndSave()

            return monthDistanceFromSP
        }
        set(value) {
            saveMonthDistanceToSP(value)
        }

    private fun saveMonthDistanceToSP(value: Double) {
        authData.edit {
            putString(KEY_DISTANCE_MONTH, value.toString())
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadMonthDistanceFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForMonthDistanceChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForMonthDistanceChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            saveMonthDistanceToSP(currentUserInfoFromDB.monthDistance)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var yearDistance: Double
        get() {
            val yearDistanceFromSP = authData.getString(KEY_DISTANCE_YEAR, "0")!!.toDouble()

            asyncLoadYearDistanceFromDBAndSave()

            return yearDistanceFromSP
        }
        set(value) {
            saveYearDistanceToSP(value)
        }

    private fun saveYearDistanceToSP(value: Double) {
        authData.edit {
            putString(KEY_DISTANCE_YEAR, value.toString())
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadYearDistanceFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForYearDistanceChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForYearDistanceChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            saveYearDistanceToSP(currentUserInfoFromDB.yearDistance)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var availableSkins: List<CatView.Skins>
        get() = try {
            val json = authData.getString(KEY_SKINS, null)!!
            val jsonArray = gson.fromJson(json, JsonElement::class.java)!!.asJsonArray!!

            val availableSkinsFromSP = mutableListOf<CatView.Skins>()

            for (jsonElement in jsonArray) {
                jsonElement ?: continue

                availableSkinsFromSP +=
                        gson.fromJson(jsonElement, CatView.Skins::class.java)
                        ?: continue
            }

            availableSkinsFromSP
        } catch (e: Exception) {
            emptyList()
        } finally {
            asyncLoadAvailableSkinsFromDBAndSave()
        }
        set(value) {
            saveAvailableSkinsToSP(value)
        }

    private fun saveAvailableSkinsToSP(value: List<CatView.Skins>) {
        authData.edit {
            putString(KEY_SKINS, gson.toJson(value))
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadAvailableSkinsFromDBAndSave() {
        RxFirestore.getCollection(currentUserChallengesCollection)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetChallengesCollectionForAvailableSkins(it) },
                { report(it) }
            )
    }

    private fun onGetChallengesCollectionForAvailableSkins(currentUserChallengesSnapshot: QuerySnapshot) {
        try {
            val currentUserChallenges = getCurrentUserChallenges(currentUserChallengesSnapshot)

            val completedChallenges = currentUserChallenges.filter { it.isComplete }

            val availableSkins = mutableSetOf<CatView.Skins>()

            for (completedChallenge in completedChallenges) {
                val availableSkin = CatView.Skins.valueOf(completedChallenge.reward)

                availableSkins += availableSkin
            }

            saveAvailableSkinsToSP(availableSkins.toList())
        } catch (e: Exception) {
            report(e)
        }
    }

    private fun getCurrentUserChallenges(currentUserChallengesSnapshot: QuerySnapshot): List<Challenge> {
        val currentUserChallenges =
            currentUserChallengesSnapshot
                .toObjects(Challenge::class.java)
                .toSet()
                .toMutableList()

        //почему-то у некоторых выполненных челленджей из БД isComplete == false, хотя в БД он true
        //поэтому фиксим
        for (currentUserChallengeSnapshot in currentUserChallengesSnapshot.documents) {
            val data = currentUserChallengeSnapshot.data ?: continue

            val isComplete = data["isComplete"]?.toString()?.toBoolean() ?: continue

            val reward = data["reward"]?.toString() ?: continue

            val invalidChallenge =
                currentUserChallenges.find { it.reward == reward } ?: continue

            val indexOfInvalidChallenge =
                currentUserChallenges.indexOf(invalidChallenge)

            currentUserChallenges[indexOfInvalidChallenge] =
                    invalidChallenge.copy(isComplete = isComplete)
        }

        return currentUserChallenges.toList()
    }

    override var currentSkin: CatView.Skins
        get() = try {
            val json = authData.getString(KEY_CURRENT_SKIN, null)!!

            gson.fromJson(json, CatView.Skins::class.java)!!
        } catch (e: Exception) {
            CatView.Skins.COMMON
        } finally {
            asyncLoadCurrentSkinFromDBAndSave()
        }
        set(value) {
            saveCurrentSkinToSP(value)
        }

    private fun saveCurrentSkinToSP(value: CatView.Skins) {
        authData.edit {
            putString(KEY_CURRENT_SKIN, gson.toJson(value))
        }
    }

    @SuppressLint("CheckResult")
    private fun asyncLoadCurrentSkinFromDBAndSave() {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForCurrentSkinChange(it) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForCurrentSkinChange(currentUser: DocumentSnapshot) {
        try {
            val currentUserInfoFromDB = currentUser.toObject(DBUser::class.java) ?: return

            val skin = CatView.Skins.valueOf(currentUserInfoFromDB.costume)

            saveCurrentSkinToSP(skin)
        } catch (e: Exception) {
            report(e)
        }
    }

    override var completedChallenges: List<ChallengeItem>
        get() = try {
            val json = authData.getString(KEY_CHALLENGES, null)!!
            val jsonArray = gson.fromJson(json, JsonElement::class.java)!!.asJsonArray!!
            val completedChallengesFromSP = mutableListOf<ChallengeItem>()

            for (jsonElement in jsonArray) {
                jsonElement ?: continue

                completedChallengesFromSP +=
                        gson.fromJson(jsonElement, ChallengeItem::class.java)
                        ?: continue
            }

            completedChallengesFromSP
        } catch (e: Exception) {
            emptyList()
        } finally {
            asyncLoadCompletedChallengesFromDBAndSave()
        }
        set(value) {
            saveCompletedChallengesToSP(value)
        }

    private fun saveCompletedChallengesToSP(value: List<ChallengeItem>) {
        authData.edit {
            putString(KEY_CHALLENGES, gson.toJson(value))
        }
    }

    private val currentUserChallengesCollection
        get() = currentUserDocument.collection(challengesCollection)

    @SuppressLint("CheckResult")
    private fun asyncLoadCompletedChallengesFromDBAndSave() {
        RxFirestore.getCollection(currentUserChallengesCollection)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetChallengesCollectionForCompletedChallenges(it) },
                { report(it) }
            )
    }

    private fun onGetChallengesCollectionForCompletedChallenges(currentUserChallengesSnapshot: QuerySnapshot) {
        try {
            val currentUserChallenges = getCurrentUserChallenges(currentUserChallengesSnapshot)

            val completedChallenges = currentUserChallenges.filter { it.isComplete }

            val dbChallengeItems = mutableSetOf<ChallengeItem>()

            for (@Suppress("Destructure") completedChallenge in completedChallenges) {
                val (
                        distance,
                        hour,
                        id,
                        _,
                        isComplete,
                        minutes,
                        reward
                ) = completedChallenge

                val award = when (reward) {
                    CatView.Skins.COMMON.name -> R.drawable.normal_cat_sportsuniform
                    CatView.Skins.BAD.name -> R.drawable.bad_cat_jacket
                    CatView.Skins.BUSINESS.name -> R.drawable.bussiness_cat_cloth
                    CatView.Skins.KARATE.name -> R.drawable.karate_cat_kimono
                    CatView.Skins.NORMAL.name -> R.drawable.normal_cat_sportsuniform
                    else -> throw IllegalStateException("Wrong reward")
                }

                dbChallengeItems += ChallengeItem(
                    id = id,
                    distance = distance.roundToInt(),
                    time = (minutes + hour / 60).toInt(),
                    awardRes = award,
                    isComplete = isComplete
                )
            }

            saveCompletedChallengesToSP(dbChallengeItems.toList())
        } catch (e: Exception) {
            report(e)
        }

        val dbChallenges = mutableSetOf<ChallengeItem>()

        for (document in currentUserChallengesSnapshot.documents) {
            val challenge = document.toObject(Challenge::class.java) ?: continue

            val award = when (challenge.reward) {
                CatView.Skins.COMMON.name -> R.drawable.normal_cat_sportsuniform
                CatView.Skins.BAD.name -> R.drawable.bad_cat_jacket
                CatView.Skins.BUSINESS.name -> R.drawable.bussiness_cat_cloth
                CatView.Skins.KARATE.name -> R.drawable.karate_cat_kimono
                CatView.Skins.NORMAL.name -> R.drawable.normal_cat_sportsuniform
                else -> throw IllegalStateException("Wrong reward")
            }

            dbChallenges += ChallengeItem(
                id = challenge.id,
                distance = challenge.distance.roundToInt(),
                time = (challenge.minutes + challenge.hour / 60).toInt(),
                awardRes = award,
                isComplete = challenge.isComplete
            )
        }

        saveCompletedChallengesToSP(dbChallenges.toList())
    }

    private fun report(throwable: Throwable) {
        Timber.e(throwable)
        Crashlytics.logException(throwable)
        YandexMetrica.reportUnhandledException(throwable)
    }

    private inline fun SharedPreferences.edit(
        action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = edit()
        action(editor)
        editor.apply()
    }

}