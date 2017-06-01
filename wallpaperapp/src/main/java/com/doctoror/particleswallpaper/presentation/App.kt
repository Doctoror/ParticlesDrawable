package com.doctoror.particleswallpaper.presentation

import android.app.Application
import com.doctoror.particleswallpaper.BuildConfig
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.doctoror.particleswallpaper.presentation.di.components.DaggerConfigComponent
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule
import android.os.StrictMode

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initStrictMode()
        initDagger()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }
    }

    private fun initDagger() {
        Injector.configComponent = DaggerConfigComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}