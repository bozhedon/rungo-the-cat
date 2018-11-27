package com.myrungo.rungo

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.myrungo.rungo.auth.AuthHolder
import com.myrungo.rungo.cat.CatController
import com.myrungo.rungo.model.*
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.fabric.sdk.android.Fabric
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import timber.log.Timber
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.configuration.Configuration
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appCode = UUID.randomUUID().toString()

        initLogger()
        initFabric()
        initYandexMetrica()
        initToothpick()
        initAppScope()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(
                Fabric.Builder(this)
                    .kits(Crashlytics())
                    .build()
            )
        }
    }

    private fun initYandexMetrica() {
        val config = YandexMetricaConfig
            .newConfigBuilder("bb71ae13-3e41-4920-9ac2-0258337217b0")
            .withLocationTracking(false)
            .withCrashReporting(true)
            .withStatisticsSending(true)
            .withNativeCrashReporting(true)
            .withInstalledAppCollecting(true)
            .build()

        YandexMetrica.activate(this, config)
        YandexMetrica.setLocationTracking(false)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            //Toothpick.setConfiguration(Configuration.forProduction().disableReflection())
            //FactoryRegistryLocator.setRootRegistry(com.myrungo.rungo.FactoryRegistry())
            //MemberInjectorRegistryLocator.setRootRegistry(com.myrungo.rungo.MemberInjectorRegistry())
        }
    }

    private fun initAppScope() {
        Toothpick.openScope(Scopes.APP)
            .installModules(
                object : Module() {
                    init {
                        bind(Context::class.java).toInstance(this@App)
                        bind(SchedulersProvider::class.java).toInstance(AppSchedulers())
                        bind(ResourceManager::class.java).singletonInScope()
                        bind(MainNavigationController::class.java).toInstance(MainNavigationController())
                        bind(CatController::class.java).toInstance(CatController())

                        bind(Gson::class.java).toInstance(Gson())
                        bind(AuthHolder::class.java).to(Prefs::class.java).singletonInScope()

                        val cicerone = Cicerone.create()
                        bind(Router::class.java).toInstance(cicerone.router)
                        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)
                    }
                }
            )
    }

    companion object {
        lateinit var appCode: String
            private set
    }
}