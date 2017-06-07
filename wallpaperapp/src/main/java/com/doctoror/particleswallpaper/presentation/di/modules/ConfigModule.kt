package com.doctoror.particleswallpaper.presentation.di.modules

import android.content.Context
import android.content.res.Resources
import com.doctoror.particleswallpaper.data.config.DrawableConfiguratorImpl
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryDefault
import com.doctoror.particleswallpaper.data.repository.SettingsRepositoryImpl
import com.doctoror.particleswallpaper.domain.config.DrawableConfigurator
import com.doctoror.particleswallpaper.domain.repository.MutableSettingsRepository
import com.doctoror.particleswallpaper.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
@Module
class ConfigModule {

    companion object {
        const val DEFAULT = "default"
    }

    @Named(DEFAULT) @Singleton @Provides fun provideDefaultSettings(res: Resources):
            SettingsRepository = SettingsRepositoryDefault(res)

    @Singleton @Provides fun provideMutableSettings(context: Context):
            MutableSettingsRepository = SettingsRepositoryImpl(context)

    @Singleton @Provides fun provideSettings(settings: MutableSettingsRepository):
            SettingsRepository = settings

    @Provides fun provideDrawableConfigurator():
            DrawableConfigurator = DrawableConfiguratorImpl()

}