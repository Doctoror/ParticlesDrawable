package com.doctoror.particleswallpaper.presentation

import android.app.Application
import com.doctoror.particleswallpaper.presentation.di.Injector
import com.doctoror.particleswallpaper.presentation.di.components.DaggerConfigComponent
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        Injector.configComponent = DaggerConfigComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}