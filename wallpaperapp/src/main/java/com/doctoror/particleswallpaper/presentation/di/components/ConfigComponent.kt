package com.doctoror.particleswallpaper.presentation.di.components

import com.doctoror.particleswallpaper.data.engine.WallpaperServiceImpl
import com.doctoror.particleswallpaper.presentation.config.ConfigActivity
import com.doctoror.particleswallpaper.presentation.config.ConfigActivityLollipop
import com.doctoror.particleswallpaper.presentation.di.modules.AdsModule
import com.doctoror.particleswallpaper.presentation.di.modules.AppModule
import com.doctoror.particleswallpaper.presentation.di.modules.ConfigModule
import com.doctoror.particleswallpaper.presentation.preference.*
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, ConfigModule::class, AdsModule::class))
interface ConfigComponent {

    fun inject(p: WallpaperServiceImpl.EngineImpl)

    fun inject(t: ConfigActivity)
    fun inject(t: ConfigActivityLollipop)

    fun inject(p: BackgroundColorPreference)
    fun inject(p: BackgroundImagePreference)
    fun inject(p: DotScalePreference)
    fun inject(p: FrameDelayPreference)
    fun inject(p: LicensePreference)
    fun inject(p: LineDistancePreference)
    fun inject(p: LineScalePreference)
    fun inject(p: NumDotsPreference)
    fun inject(p: ParticlesColorPreference)
    fun inject(p: PerformanceTipsPreference)
    fun inject(p: PreviewPreference)
    fun inject(p: ResetToDefaultsPreference)
    fun inject(p: SpeedFactorPreference)
}