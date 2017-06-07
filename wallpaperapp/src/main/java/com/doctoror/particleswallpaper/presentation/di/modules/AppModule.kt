package com.doctoror.particleswallpaper.presentation.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Yaroslav Mytkalyk on 01.06.17.
 */
@Module
class AppModule constructor(val context: Context) {

    @Singleton @Provides fun provideContext() = context
    @Singleton @Provides fun provideResources() = context.resources!!

}