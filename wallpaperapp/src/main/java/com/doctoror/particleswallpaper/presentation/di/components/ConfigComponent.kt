package com.doctoror.particleswallpaper.presentation.di.components

import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.presentation.config.ConfigActivity
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityLollipop
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import com.doctoror.particleswallpaper.presentation.preference.*
import com.doctoror.particleswallpaper.presentation.presenter.BackgroundColorPreferencePresenter
import com.doctoror.particleswallpaper.presentation.presenter.BackgroundImagePreferencePresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, ConfigModule::class))
interface ConfigComponent {

    fun inject(p: WallpaperServiceImpl.EngineImpl)

    fun inject(t: ConfigActivity)
    fun inject(t: ConfigActivityLollipop)

    fun inject(p: BackgroundColorPreference)
    fun inject(p: BackgroundImagePreference)
    fun inject(p: DotScalePreference)
    fun inject(p: FrameDelayPreference)
    fun inject(p: LineDistancePreference)
    fun inject(p: LineScalePreference)
    fun inject(p: NumDotsPreference)
    fun inject(p: ParticlesColorPreference)
    fun inject(p: ResetToDefaultPreference)
    fun inject(p: SpeedFactorPreference)

    fun inject(p: BackgroundColorPreferencePresenter)
    fun inject(p: BackgroundImagePreferencePresenter)

}