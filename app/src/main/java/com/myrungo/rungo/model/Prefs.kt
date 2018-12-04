package com.myrungo.rungo.model

import android.annotation.SuppressLint
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

    private val currentUser
        get() = FirebaseAuth.getInstance().currentUser
            ?: throw RuntimeException("User must sign in app")

    private val currentUserDocument
        get() = FirebaseFirestore.getInstance()
            .collection(usersCollection)
            .document(currentUser.uid)

    override var name: String
        get() {
            val name = getSharedPreferences(AUTH_DATA).getString(KEY_NAME, null) ?: "User"

            checkNameMatch(name)

            return name
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_NAME, value)
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkNameMatch(name: String) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForNameChange(it, name) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForNameChange(documentSnapshot: DocumentSnapshot, spName: String) {
        documentSnapshot.data?.let { data ->
            data[userNameKey]?.toString()?.let { dbName ->
                if (spName != dbName) {
                    saveToDBTheName(spName)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheName(nameValue: String) {
        RxFirestore.updateDocument(currentUserDocument, userNameKey, nameValue)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var distance: Double
        get() {
            val distanceValue =
                getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE, "0")?.toDoubleOrNull()
                    ?: 0.toDouble()

            checkDistanceValueMatch(distanceValue)

            return distanceValue
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE, value.toString())
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkDistanceValueMatch(distanceValue: Double) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForDistanceChange(it, distanceValue) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForDistanceChange(
        documentSnapshot: DocumentSnapshot,
        spDistanceValue: Double
    ) {
        documentSnapshot.data?.let { data ->
            data[userTotalDistanceKey]?.toString()?.toDouble()?.let { dbDistanceValue ->
                if (spDistanceValue != dbDistanceValue) {
                    saveToDBTheDistanceValue(spDistanceValue)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheDistanceValue(spDistanceValue: Double) {
        RxFirestore.updateDocument(currentUserDocument, userTotalDistanceKey, spDistanceValue)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var weekDistance: Double
        get() {
            val weekDistance =
                getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_WEEK, "0")?.toDoubleOrNull()
                    ?: 0.toDouble()

            checkWeekDistanceMatch(weekDistance)

            return weekDistance
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_WEEK, value.toString())
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkWeekDistanceMatch(spWeekDistance: Double) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForWeekDistanceChange(it, spWeekDistance) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForWeekDistanceChange(
        documentSnapshot: DocumentSnapshot,
        spWeekDistance: Double
    ) {
        documentSnapshot.data?.let { data ->
            data[userWeekDistanceKey]?.toString()?.toDouble()?.let { dbWeekDistanceValue ->
                if (spWeekDistance != dbWeekDistanceValue) {
                    saveToDBTheWeekDistanceValue(spWeekDistance)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheWeekDistanceValue(spWeekDistance: Double) {
        RxFirestore.updateDocument(currentUserDocument, userWeekDistanceKey, spWeekDistance)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var monthDistance: Double
        get() {
            val spMonthDistance = (getSharedPreferences(AUTH_DATA).getString(
                KEY_DISTANCE_MONTH,
                "0"
            )?.toDoubleOrNull()
                ?: 0.toDouble())

            checkMonthDistanceMatch(spMonthDistance)

            return spMonthDistance
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_MONTH, value.toString())
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkMonthDistanceMatch(spMonthDistance: Double) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForMonthDistanceChange(it, spMonthDistance) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForMonthDistanceChange(
        documentSnapshot: DocumentSnapshot,
        spMonthDistance: Double
    ) {
        documentSnapshot.data?.let { data ->
            data[userMonthDistanceKey]?.toString()?.toDouble()?.let { dbWeekDistanceValue ->
                if (spMonthDistance != dbWeekDistanceValue) {
                    saveToDBTheMonthDistanceValue(spMonthDistance)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheMonthDistanceValue(spMonthDistance: Double) {
        RxFirestore.updateDocument(currentUserDocument, userMonthDistanceKey, spMonthDistance)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var yearDistance: Double
        get() {
            val spYearDistance =
                (getSharedPreferences(AUTH_DATA).getString(KEY_DISTANCE_YEAR, "0")?.toDoubleOrNull()
                    ?: 0.toDouble())

            checkYearDistanceMatch(spYearDistance)

            return spYearDistance
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_DISTANCE_YEAR, value.toString())
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkYearDistanceMatch(spYearDistance: Double) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForYearDistanceChange(it, spYearDistance) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForYearDistanceChange(
        documentSnapshot: DocumentSnapshot,
        spYearDistance: Double
    ) {
        documentSnapshot.data?.let { data ->
            data[userYearDistanceKey]?.toString()?.toDouble()?.let { dbWeekDistanceValue ->
                if (spYearDistance != dbWeekDistanceValue) {
                    saveToDBTheYearDistanceValue(spYearDistance)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheYearDistanceValue(spYearDistance: Double) {
        RxFirestore.updateDocument(currentUserDocument, userYearDistanceKey, spYearDistance)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
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
        RxFirestore.getCollection(currentUserChallengesCollection)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetChallengesCollectionForAvailableSkins(it, availableSkins) },
                { report(it) }
            )
    }

    private fun onGetChallengesCollectionForAvailableSkins(
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
                saveToDBTheSkin(skinForSave)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheSkin(skinForSave: CatView.Skins) {
        val challengeInfo = mutableMapOf<String, Any>()

        challengeInfo[challengeDistanceKey] = 0
        challengeInfo[challengeHourKey] = 0
        challengeInfo[challengeIdKey] = 0
        challengeInfo[challengeImgURLKey] = ""
        challengeInfo[challengeIsCompleteKey] = true
        challengeInfo[challengeMinutesKey] = 0
        challengeInfo[challengeRewardKey] = skinForSave.name

        RxFirestore.addDocument(currentUserChallengesCollection, challengeInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var currentSkin: CatView.Skins
        get() = try {
            val json = getSharedPreferences(AUTH_DATA).getString(KEY_CURRENT_SKIN, null)
            val spCurrentSkin = gson.fromJson(json, CatView.Skins::class.java)!!

            checkCurrentSkinMatch(spCurrentSkin.name)

            spCurrentSkin
        } catch (e: Exception) {
            CatView.Skins.COMMON
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_CURRENT_SKIN, gson.toJson(value))
                .apply()
        }

    @SuppressLint("CheckResult")
    private fun checkCurrentSkinMatch(spCurrentSkin: String) {
        RxFirestore.getDocument(currentUserDocument)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetUserDocumentForCurrentSkinChange(it, spCurrentSkin) },
                { report(it) }
            )
    }

    private fun onGetUserDocumentForCurrentSkinChange(
        documentSnapshot: DocumentSnapshot,
        spCurrentSkin: String
    ) {
        documentSnapshot.data?.let { data ->
            data[userCurrentSkinKey]?.toString()?.let { dbWeekDistanceValue ->
                if (spCurrentSkin != dbWeekDistanceValue) {
                    saveToDBTheCurrentSkinValue(spCurrentSkin)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBTheCurrentSkinValue(spCurrentSkin: String) {
        RxFirestore.updateDocument(currentUserDocument, userCurrentSkinKey, spCurrentSkin)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    override var completedChallenges: List<ChallengeItem>
        get() = try {
            val json = getSharedPreferences(AUTH_DATA).getString(KEY_CHALLENGES, null)
            val jsonArray = gson.fromJson(json, JsonElement::class.java).asJsonArray
            val spCompletedChallenges =
                jsonArray.map { gson.fromJson(it, ChallengeItem::class.java) }

            checkCompletedChallenges(spCompletedChallenges)

            spCompletedChallenges
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            getSharedPreferences(AUTH_DATA)
                .edit()
                .putString(KEY_CHALLENGES, gson.toJson(value))
                .apply()
        }

    private val currentUserChallengesCollection
        get() = currentUserDocument.collection(challengesCollection)

    @SuppressLint("CheckResult")
    private fun checkCompletedChallenges(spCompletedChallenges: List<ChallengeItem>) {
        RxFirestore.getCollection(currentUserChallengesCollection)
            .subscribeOn(schedulers.io())
            .subscribeOn(schedulers.ui())
            .subscribe(
                { onGetChallengesCollectionForCompletedChallenges(it, spCompletedChallenges) },
                { report(it) }
            )
    }

    private fun onGetChallengesCollectionForCompletedChallenges(
        snapshot: QuerySnapshot,
        spCompletedChallengesList: List<ChallengeItem>
    ) {
        val dbChallenges = mutableSetOf<Pair<Int, Boolean>>()

        for (document in snapshot.documents) {
            document.data?.let { data ->
                val dbIsComplete = data[challengeIsCompleteKey]?.toString()?.toBoolean()
                val dbID = data[challengeIdKey]?.toString()?.toIntOrNull()

                if (dbIsComplete != null && dbID != null) {
                    dbChallenges += dbID to dbIsComplete
                }
            }
        }

        val spCompletedChallenges = spCompletedChallengesList.map { it.id to it.isComplete }.toSet()

        val completedChallengesThatDbDoesNotContain = spCompletedChallenges.minus(dbChallenges)

        for (completedChallenge in completedChallengesThatDbDoesNotContain) {
            //shared preferences contains completed challenge, that does not exist in DB
            saveToDBCompletedChallenge(
                completedChallenge,
                spCompletedChallengesList.find { completedChallenge.first == it.id }!!
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun saveToDBCompletedChallenge(
        completedChallenge: Pair<Int, Boolean>,
        completedChallengeItem: ChallengeItem
    ) {
        val challengeInfo = mutableMapOf<String, Any>()

        challengeInfo[challengeDistanceKey] = 0
        challengeInfo[challengeHourKey] = 0
        challengeInfo[challengeIdKey] = completedChallenge.first
        challengeInfo[challengeImgURLKey] = ""
        challengeInfo[challengeIsCompleteKey] = completedChallenge.second
        challengeInfo[challengeMinutesKey] = 0
        challengeInfo[challengeRewardKey] = completedChallengeItem.awardRes

        RxFirestore.addDocument(currentUserChallengesCollection, challengeInfo)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { },
                { report(it) }
            )
    }

    private fun report(throwable: Throwable) {
        Timber.e(throwable)
        Crashlytics.logException(throwable)
        YandexMetrica.reportUnhandledException(throwable)
    }
}